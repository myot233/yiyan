package com.example.yiyan.controller;

import com.example.yiyan.baidu.BaiDuApiCaller;
import com.example.yiyan.baidu.OcrImpl;
import com.example.yiyan.baidu.OcrInterFace;
import com.example.yiyan.baidu.OcrResult;
import com.example.yiyan.common.BaseResponse;
import com.example.yiyan.common.ResultUtils;
import com.example.yiyan.constant.TransConstant;
import com.example.yiyan.model.dto.*;
import com.example.yiyan.model.vo.MessageResponse;
import com.example.yiyan.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.JsonObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ArrayUtil.append;

/**
 * 此类用于定义TrafficController类
 * @Version 1.0
 * @Description 主要的接口，由于文心文档建议接口数量不超过20个，就将所有的接口都放在这里了
 *
 */

@RestController
@Slf4j
@CrossOrigin
public class TrafficController {
    Logger logger = LoggerFactory.getLogger(TrafficController.class);

    public static final String ROUTE_PLANNING_PROMPT = ResourceUtil.getResourceAsString("/prompt/route_planning_prompt");

    public static final String PLUGIN_DESCRIPTION_PROMPT = ResourceUtil.getResourceAsString("/prompt/plugin_description_prompt");
    private final BaiDuApiCaller caller = new BaiDuApiCaller();
    /**
     * 通过请求天气api（例如百度地图或风天气）获取天气信息，返回过去2小时和未来一天的天气状况
     * 交给文心一言总结润色
     * @param request
     * @return
     */
    @PostMapping("/city_weather")
    public ResponseEntity<MessageResponse> CityWeather(@RequestBody CityWeatherRequest request) throws Exception {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("district_id", request.getDistrictId());
        params.put("data_type", request.getDataType());
        params.put("ak", BaiDuApiCaller.AK2);

        String cityWeather = caller.requestCityWeather(params);

        return ResponseEntity.ok(new MessageResponse(cityWeather));
    }

    /**
     * 请求地图api（例如百度地图）获取地点信息的地图，返回地图的公网地址
     * @param request
     * @return
     */
    @PostMapping("/location_map")
    public ResponseEntity<MessageResponse> LocationMap(@RequestBody LocationMapRequest request) throws Exception {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("center", request.getCenter());
        params.put("ak", BaiDuApiCaller.AK);

        String cityWeather = caller.requestLocationMap(params);

        return ResponseEntity.ok(new MessageResponse(cityWeather));
    }



    /**
     * 请求路线规划api（百度地图）获取路线规划信息，返回路线规划的文本（如果有图片最好）
     * 参考百度地图有：
     * 路线规划
     * 驾车路线规划(轻量)
     * 骑行路线规划(轻量)
     * 步行路线规划(轻量)
     * 公交路线规划(轻量)
     * 五种api，可以先初步实现驾车线路规划
     *
     * @param request
     * @return
     */
    @PostMapping("/route_planning")
    public BaseResponse<Map<String,String>> RoutePlanning(@RequestBody RoutePlanningRequest request) throws Exception {
        logger.info("/route_panning is called");
        String Message = "出现未知错误";
        String mapUrl = "";
        Map<String,String> params = new LinkedHashMap<>();
        params.put("origin", request.getOriginPoint(caller));
        params.put("destination", request.getDestinationPoint(caller));
        params.put("ak", BaiDuApiCaller.AK);
        return getMapBaseResponse(
                request.getWay(),
                request.getOrigin(),
                request.getDestination(),
                params
        );
    }

    @NotNull
    private BaseResponse<Map<String, String>> getMapBaseResponse(String way,String origin,String destination,
                                                                 Map<String, String> params) throws Exception {
        String Message = "出现未知错误";
        String mapUrl = "";
        try {
            JsonObject jsonObject = caller.requestRoutePlanning(way, params);
            if (!jsonObject.get("message").getAsString().equals("ok")){
                Message = "使用的人太多了，请稍后再试";
            }else{
                JsonObject result = jsonObject.getAsJsonObject("result");
                JsonElement routesElement = result.getAsJsonArray("routes").get(0);
                JsonObject routesObject = routesElement.getAsJsonObject();
                JsonArray steps = routesObject.getAsJsonArray("steps");
                // 改用StringBuffer拼接路径规划的基本信息
                StringBuilder stringBuffer = new StringBuilder("起点");
                stringBuffer.append(origin).append(",").append("终点：")
                        .append(destination).append(",").append("距离：")
                        .append(routesObject.get("distance")).append("米,").append("耗时：")
                        .append(routesObject.get("duration")).append("秒,");
                        //.append("路况：" + TransConstant.traffic_conditions[routesObject.get("traffic_condition").getAsInt()] + ",")
                        if(routesObject.get("traffic_condition") != null){
                            stringBuffer.append("路况：")
                                    .append(TransConstant.traffic_conditions[routesObject.get("traffic_condition").getAsInt()])
                                    .append(",");
                        }
                        stringBuffer.append("导航规划路径:");
                // 拼接详细路径和绘制导航静态图
                List<ArrayList<Float>> paths = new ArrayList<>();
                for (JsonElement step :
                        steps) {
                    JsonObject stepObject = step.getAsJsonObject();
                    ArrayList<Float> pathPoint = new ArrayList<>();
                    pathPoint.add(stepObject.get("start_location").getAsJsonObject().get("lat").getAsFloat());
                    pathPoint.add(stepObject.get("start_location").getAsJsonObject().get("lng").getAsFloat());
                    paths.add(pathPoint);
                }
                List<JsonObject> placesConcurrent =  findPlacesDummy(paths);
                for(int i = 0;i < steps.size();i++){
                    stringBuffer.append(steps.get(i).getAsJsonObject().get("instruction"));
                    placesConcurrent.get(i)
                            .getAsJsonArray("results")
                            .forEach(x->stringBuffer.append("附近的店:")
                                    .append(x.getAsJsonObject().get("name").getAsString()).append(";"));

                }


                Message = stringBuffer.toString();
                JsonElement lastStep = steps.get(steps.size() - 1);
                JsonObject lastStepObject = lastStep.getAsJsonObject();
                ArrayList<Float> pathPoint = new ArrayList<>();
                pathPoint.add(lastStepObject.get("end_location").getAsJsonObject().get("lat").getAsFloat());
                pathPoint.add(lastStepObject.get("end_location").getAsJsonObject().get("lng").getAsFloat());
                paths.add(pathPoint);
                mapUrl = DrawMapUtil.getMap(paths);
            }
        } catch (com.google.gson.JsonSyntaxException e) {
            logger.info("gson转换错误",e);
        }
        Map<String,String> response = new LinkedHashMap<>();
        response.put("message", Message);
        response.put("mapUrl", mapUrl);

        response.put("prompt", ROUTE_PLANNING_PROMPT);
        logger.info(Message);
        return ResultUtils.success(response);
    }

    private List<JsonObject> findPlacesDummy(List<ArrayList<Float>> paths) {
        String[] selecetions = new String[]{"水果摊","鞋垫","小学","书店","车库"};
        Gson tempGson = new Gson();
        List<JsonObject> jsonObjects = new ArrayList<>();
        paths.forEach(x->jsonObjects.add(tempGson.fromJson(String.format("{results:[{name:\"%s%s\"}]}", "杭州师范", selecetions[new Random().nextInt(selecetions.length)]),JsonObject.class)));
        return jsonObjects;
    }

    private List<JsonObject> findPlacesConcurrent(List<ArrayList<Float>> paths) {
        ExecutorService threadpool = Executors.newCachedThreadPool();
        List<Future<JsonObject>> futures = new ArrayList<>();
        for (ArrayList<Float> path : paths) {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("query", "美食");
            params.put("location", String.format("%s,%s", path.get(0),path.get(1)));
            params.put("radius", "200");
            params.put("output", "json");
            params.put("ak", BaiDuApiCaller.AK2);
            Future<JsonObject> jsonObjectFuture = threadpool.submit(() -> caller.requestRoutePlaceSearch(params));
            futures.add(jsonObjectFuture);
        }
        return futures.stream().map(x-> {
            try {
                return x.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

    }


    /**
     * 请求实时道路状况api（百度地图）获取道路状况信息，返回道路状况的文本
     * @param request
     * @return
     */
    @PostMapping("/Road_condition")
    public ResponseEntity<MessageResponse> RoadCondition(@RequestBody RoadConditionRequest request) throws Exception {
        RoadConditionUtil roadConditionUtil = new RoadConditionUtil();

        // 构建参数映射
        Map<String, String> params = new LinkedHashMap<>();
        params.put("road_name", request.getRoadName());
        params.put("city", request.getCity());
        params.put("ak", RoadConditionUtil.AK);

        // 调用工具类的方法获取道路状况信息
        String roadCondition = caller.requestRoadCondition(params);

        // 返回包含状况信息文本的响应
        return ResponseEntity.ok(new MessageResponse(roadCondition));
    }


    @PostMapping("get_description")
    public BaseResponse<Map<String,String>> getDescription() throws Exception {
        Gson gson = new Gson();
        Map<String,String> params = new HashMap<>();
        JsonObject detail = gson.fromJson(ResourceUtil.getResourceAsString("/.well-known/ai-plugin.json"),JsonObject.class);
        params.put("pluginName",detail.get("name_for_human").getAsString());
        params.put("pluginDescription",detail.get("description_for_human").getAsString());
        params.put("pluginUrl",detail.get("legal_info_url").getAsString());
        params.put("prompt",PLUGIN_DESCRIPTION_PROMPT);
        return ResultUtils.success(params);
    }

    /**
     * 获取图像的URL
     * @param request
     * @return
     */
    @PostMapping("/get_image")
    public BaseResponse<Map<String,String>> getImage(@RequestBody ImageRequest request) throws Exception {

        OcrInterFace ocr = new OcrImpl();
        String url = request.getUrl();
//        System.out.println(imageUrl);
        logger.info(url);
        String prompt = "以下是路线的图片链接: ![img](图片链接) 请参考以上我给出的例子输出, url是图片的连接url";
        Map<String,String> response = new LinkedHashMap<>();
        response.put("url", url);
        response.put("prompt", prompt);
        Path img = downloadImage(url);
        OcrResult result = ocr.getPlaceFromTicket(img);

        if(result == null){
            result = ocr.getPlaceFromImage(img);
        }
        if(result == null){
            return null;
        }
        response.put("result",result.toString());
        Map<String,String> params = new LinkedHashMap<>();
        params.put("origin", request.getOriginPoint(caller));
        params.put("destination", result.getOriginPoint(caller));
        params.put("ak", BaiDuApiCaller.AK);
        return getMapBaseResponse(
                request.getWay(),
                request.getStartPos(),
                result.getStartPlace(),
                params
        );

    }



    public static Path downloadImage(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                InputStream inputStream = body.byteStream();
                Path imagePath = Files.createTempFile("image", ".jpg");
                saveImage(inputStream, imagePath);
                return imagePath;
            } else {
                System.out.println("Error: Response body is null.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveImage(InputStream inputStream, Path imagePath) {
        try (OutputStream outputStream = new FileOutputStream(imagePath.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Image downloaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


