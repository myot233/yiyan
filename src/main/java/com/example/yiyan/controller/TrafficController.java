package com.example.yiyan.controller;

import com.example.yiyan.baidu.BaiDuApiCaller;
import com.example.yiyan.common.BaseResponse;
import com.example.yiyan.common.ResultUtils;
import com.example.yiyan.constant.TransConstant;
import com.example.yiyan.model.dto.*;
import com.example.yiyan.model.vo.MessageResponse;
import com.example.yiyan.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static final String ROUTE_PLANNING_PROMPT =
            "message中包含关于路线的距离、耗时、路况、导航规划路径等信息，请你整合数据，以一种对用户友好的格式展示给用户。" +
                    "map_url是图片链接,请用mark语法将这个链接展示给用户, 以下是一个例子: " +
                    "距离:｛message中的距离｝\n耗时:{message中的耗时}\n导航规划路径:｛第一条路径｝\n{第二条路径}...{第n条路径}\n{用图片的形式展示map_url}";
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
        try {
            JsonObject jsonObject = caller.requestRoutePlanning(request.getWay(),params);
            if (!jsonObject.get("message").getAsString().equals("ok")){
                Message = "使用的人太多了，请稍后再试";
            }else{
                JsonObject result = jsonObject.getAsJsonObject("result");
                JsonElement routesElement = result.getAsJsonArray("routes").get(0);
                JsonObject routesObject = routesElement.getAsJsonObject();
                JsonArray steps = routesObject.getAsJsonArray("steps");
                // 改用StringBuffer拼接路径规划的基本信息
                StringBuilder stringBuffer = new StringBuilder("起点");
                stringBuffer.append(request.getOrigin()).append(",").append("终点：")
                        .append(request.getDestination()).append(",").append("距离：")
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
                    stringBuffer.append(stepObject.get("instruction")).append(",");
                    ArrayList<Float> pathPoint = new ArrayList<>();
                    pathPoint.add(stepObject.get("start_location").getAsJsonObject().get("lat").getAsFloat());
                    pathPoint.add(stepObject.get("start_location").getAsJsonObject().get("lng").getAsFloat());
                    paths.add(pathPoint);
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
}
