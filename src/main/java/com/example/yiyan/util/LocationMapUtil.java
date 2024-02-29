package com.example.yiyan.util;

//import org.springframework.web.util.UriUtils;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import org.springframework.web.util.UriUtils;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//public class LocationMapUtil {
//    public static String URL = "http://api.map.baidu.com/staticimage/v2/";
//    public static String AK = "6sjWjflCZmzWtbZzOQh9Yhw03DuADVjw";
//
//    public static void main(String[] args) {
//        LocationMapUtil locationMapUtil = new LocationMapUtil();
//        String mapImageUrl = locationMapUtil.getStaticMapImage("北京市天安门");
//        System.out.println("Map Image URL: " + mapImageUrl);
//    }
//
//    public String getStaticMapImage(String location) {
//        Map<String, String> params = new LinkedHashMap<>();
//        params.put("ak", AK);
//        params.put("width", "400");
//        params.put("height", "300");
//        params.put("center", location);
//
//        StringBuilder queryString = new StringBuilder(BASE_URL);
//        queryString.append("?");
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            queryString.append(entry.getKey()).append("=")
//                    .append(UriUtils.encode(entry.getValue(), "UTF-8")).append("&");
//        }
//        queryString.deleteCharAt(queryString.length() - 1);
//
//        return queryString.toString();
//    }
//}

/**
 * 默认：
 */


import org.springframework.web.util.UriUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocationMapUtil {

    public static String URL = "https://api.map.baidu.com/staticimage/v2?";

    public static String AK = "6sjWjflCZmzWtbZzOQh9Yhw03DuADVjw";

    public static void main(String[] args) throws Exception {

        LocationMapUtil snCal = new LocationMapUtil();

        Map params = new LinkedHashMap<String, String>();
        params.put("width", "280");
        params.put("height", "140");
        params.put("zoom", "10");
        params.put("center","116.43213,38.76623");
        params.put("ak", AK);


        snCal.requestGetAK(URL, params);
    }

    /**
     * 默认ak
     * 选择了ak，使用IP白名单校验：
     * 根据您选择的AK已为您生成调用代码
     * 检测到您当前的ak设置了IP白名单校验
     * 您的IP白名单中的IP非公网IP，请设置为公网IP，否则将请求失败
     * 请在IP地址为xxxxxxx的计算发起请求，否则将请求失败
     */
    public String requestGetAK(String strUrl, Map<String, String> param) throws Exception {
        if (strUrl == null || strUrl.length() <= 0 || param == null || param.size() <= 0) {
            return null;
        }

        StringBuffer queryString = new StringBuffer();
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

        InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        isr.close();
        return buffer.toString();
    }
    public String getURL() throws Exception {
        return URL;
    }

    public String getAK() throws Exception {
        return AK;
    }
}
