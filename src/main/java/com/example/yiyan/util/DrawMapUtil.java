package com.example.yiyan.util;

import com.example.yiyan.baidu.BosBuilder;
import com.google.common.io.Files;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.*;
import java.net.HttpURLConnection;
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
        Integer zoom = 10;
        String pathsParam = "";
        // 调整地图宽度和高度，可以根据具体需求进行调整
        int width = 500;
        int height = 500;

        // 设置参数
        Map<String, String> params = new LinkedHashMap<>();
        params.put("center", centerLatitude + "," + centerLongitude);
        params.put("width", String.valueOf(width));
        params.put("height", String.valueOf(height));
        params.put("zoom", String.valueOf(zoom));
        int pathSize = paths.size();
        for (int i = 0; i < pathSize; i++) {
            ArrayList<Float> pathPoint = paths.get(i);
            pathsParam += pathPoint.get(1) + "," + pathPoint.get(0);
            if (i < pathSize - 1) {
                pathsParam += ";";
            }
        }
        params.put("paths", pathsParam);
        params.put("ak", AK);

        // 请求地图
        byte[] responseBytes = snCal.requestGetAK(URL, params).getBytes();
        Files.write(responseBytes,new File("temp.png"));
        // 将字节数组转换为 MultipartFile
        InputStream inputStream = new ByteArrayInputStream(responseBytes);
        MultipartFile multipartFile = new MockMultipartFile("file", "map_image.png", "image/png", inputStream);
        BosBuilder bosBuilder = new BosBuilder();
        String fileUrl = BosBuilder.putObjectSimple(multipartFile);
        return fileUrl;
    }

    private float calculateZoom(float minLongitude, float maxLongitude, float minLatitude, float maxLatitude) {
        // 可以根据实际需求调整缩放级别的计算方法
        // 这里简单地计算路径边界框的跨度作为缩放级别
        float longitudeSpan = maxLongitude - minLongitude;
        float latitudeSpan = maxLatitude - minLatitude;
        // 根据经纬度跨度计算缩放级别，这里可以根据实际情况进行调整
        float zoom = Math.min(longitudeSpan, latitudeSpan);
        return zoom;
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
    public String requestGetAK(String strUrl, Map<String, String> param) throws Exception {
        if (strUrl == null || strUrl.length() <= 0 || param == null || param.size() <= 0) {
            return strUrl;
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
        System.out.println(queryString.toString());
        URLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();

        InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        isr.close();
        System.out.println("AK: " + buffer.toString());
        return buffer.toString();
    }

}
