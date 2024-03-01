package com.example.yiyan.util;

import com.example.yiyan.baidu.BosBuilder;
import com.google.common.io.Files;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DrawMapUtil {
    /**
     * 选择了ak或使用IP白名单校验：
     */
    public static String URL = "https://api.map.baidu.com/staticimage/v2?";

    public static String AK = "6sjWjflCZmzWtbZzOQh9Yhw03DuADVjw";

    public static String getMap(List<ArrayList<Float>> paths) throws Exception {
        DrawMapUtil snCal = new DrawMapUtil();

        // 计算路径的边界框
        float minLongitude = Float.MAX_VALUE;
        float maxLongitude = Float.MIN_VALUE;
        float minLatitude = Float.MAX_VALUE;
        float maxLatitude = Float.MIN_VALUE;
        for (ArrayList<Float> entry : paths) {
            float longitude = entry.get(0);
            float latitude = entry.get(1);
            if (longitude < minLongitude) minLongitude = longitude;
            if (longitude > maxLongitude) maxLongitude = longitude;
            if (latitude < minLatitude) minLatitude = latitude;
            if (latitude > maxLatitude) maxLatitude = latitude;
        }

        // 计算中心点经纬度
        float centerLongitude = (minLongitude + maxLongitude) / 2;
        float centerLatitude = (minLatitude + maxLatitude) / 2;

        // 计算缩放级别
        //float zoom = calculateZoom(minLongitude, maxLongitude, minLatitude, maxLatitude);
        double maxDistance = Math
                .sqrt(Math.pow((Math.max(Math.abs(maxLongitude - centerLongitude), Math.abs(minLongitude - centerLongitude))), 2) +
                        Math.pow((Math.max(Math.abs(maxLatitude - centerLatitude), Math.abs(minLatitude - centerLatitude))), 2));

        Integer zoom = calculateZoom(minLongitude, maxLongitude, minLatitude, maxLatitude);
        StringBuilder pathsParam = new StringBuilder();
        // 调整地图宽度和高度，可以根据具体需求进行调整
        int width = 500;
        int height = 500;

        // 设置参数
        Map<String, String> params = new LinkedHashMap<>();
        params.put("center", centerLatitude + "," + centerLongitude);
        params.put("width", String.valueOf(width));
        params.put("height", String.valueOf(height));
        params.put("zoom", String.valueOf(zoom));
        params.put("pathStyles", "0x1D7EFF,5,1");
        params.put("markers", paths.get(0).get(1)+"," + paths.get(0).get(0) + "|"
                + paths.get(paths.size()-1).get(1) + "," + paths.get(paths.size()-1).get(0)
        );
        params.put("markerStyles", "-1,https://api.map.baidu.com/images/marker_red.png,-1,23,25");
        int pathSize = paths.size();
        for (int i = 0; i < pathSize; i++) {
            ArrayList<Float> pathPoint = paths.get(i);
            pathsParam.append(pathPoint.get(1)).append(",").append(pathPoint.get(0)).append(";");
        }
        pathsParam.append("|");
        params.put("paths", pathsParam.toString());
        params.put("ak", AK);

        // 请求地图
        byte[] responseBytes = snCal.requestGetAK(URL, params);
        Files.write(responseBytes, new File("temp.png"));
        // 将字节数组转换为 MultipartFile
        InputStream inputStream = new ByteArrayInputStream(responseBytes);
        MultipartFile multipartFile = new MockMultipartFile("file", "map_image.png", "image/png", inputStream);
        BosBuilder bosBuilder = new BosBuilder();
        String fileUrl = BosBuilder.putObjectSimple(multipartFile);
        return fileUrl;
    }

    private static int calculateZoom(float minLongitude, float maxLongitude, float minLatitude, float maxLatitude) {
        // 计算路径边界框的经度和纬度跨度
        float longitudeSpan = maxLongitude - minLongitude;
        float latitudeSpan = maxLatitude - minLatitude;
        // 根据经纬度跨度计算地图的水平和垂直放缩级别
        float horizontalZoom = (float)(Math.log(360 / longitudeSpan) / Math.log(2));
        float verticalZoom = (float)(Math.log(180 / latitudeSpan) / Math.log(2));

        // 取水平和垂直放缩级别的较小值作为最终放缩级别
        float zoom = Math.min(horizontalZoom, verticalZoom);
        // 根据百度地图的放缩级别范围进行调整
        zoom = Math.max(3, Math.min(18, zoom)); // 注意这里范围是 3 到 21
        // 对应的比例尺
        float[] scale = {2000f, 1000f, 500f, 200f, 100f, 50f, 25f, 20f, 10f, 5f, 2f, 1f, 0.5f, 0.2f, 0.1f, 0.05f, 0.02f, 0.01f};
        // 寻找最接近的比例尺
        float closestScale = scale[(int) zoom - 3]; // zoom 范围是 3 到 21，而比例尺数组是从 2000 公里开始，所以要减去 3
        // 找到最接近的比例尺后，返回对应的放缩级别
        for (int i = 0; i < scale.length; i++) {
            if (scale[i] < closestScale) {
                return i + 4; // 再加上 3，因为数组下标是从 0 开始的，而放缩级别是从 3 开始
            }
        }
        return 18; // 如果没有找到合适的放缩级别，就返回最大值 21
    }


    /**
     * 默认ak
     * 选择了ak，使用IP白名单校验：
     * 根据您选择的AK已为您生成调用代码
     * 检测到您当前的ak设置了IP白名单校验
     * 您的IP白名单中的IP非公网IP，请设置为公网IP，否则将请求失败
     * 请在IP地址为0.0.0.0/0 外网IP的计算发起请求，否则将请求失败
     *
     * @return
     */
    public byte[] requestGetAK(String strUrl, Map<String, String> param) throws Exception {
        if (strUrl == null || strUrl.length() <= 0 || param == null || param.size() <= 0) {
            return null;
        }

        StringBuilder queryString = new StringBuilder();
        queryString.append(strUrl);
        for (Map.Entry<?, ?> pair : param.entrySet()) {
            queryString.append(pair.getKey() + "=");
            //    第一种方式使用的 jdk 自带的转码方式  第二种方式使用的 spring 的转码方法 两种均可
            //    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8").replace("+", "%20") + "&");
            queryString.append(UriUtils.encode((String) pair.getValue(), "UTF-8") + "&");
        }

        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }

        java.net.URL url = new URL(queryString.toString());
        System.out.println(queryString);
        URLConnection httpConnection = url.openConnection();
        httpConnection.connect();

        InputStream inputStream = httpConnection.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            byteArrayOutputStream.write(buf, 0, len);
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

}
