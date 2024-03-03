package com.example.yiyan.baidu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaiDuApiCaller {
    //public static final String AK = "6sjWjflCZmzWtbZzOQh9Yhw03DuADVjw";
    //public static final String AK2 ="W12IIMVO4gkKB66T6vNG9L9AfF2hVJSI" ;
    public static final String AK = "jVmeLesQKGXtBKVqrGurF2MEvENa509h";

    public static final String AK2 ="jVmeLesQKGXtBKVqrGurF2MEvENa509h" ;
    private static final String DRAW_MAP_URL = "https://api.map.baidu.com/staticimage/v2?";
    private static final String CITY_WEATHER_URL = "https://api.map.baidu.com/weather/v1/?";

    private static final String LOCATION_MAP_URL = "https://api.map.baidu.com/staticimage/v2?";

    private static final String PLACE_SEARCH_URL = "https://api.map.baidu.com/place/v2/search?";

    private static final String ROAD_CONDITION_URL = "https://api.map.baidu.com/traffic/v1/road?";

    private static final String ROUTE_PLANNING_URL = "https://api.map.baidu.com/directionlite/v1/";

    private static final String PLACE_LOCATION_URL = "https://api.map.baidu.com/place/v2/search?";

    private final Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger(BaiDuApiCaller.class);

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
        logger.info(param.toString());
        return buffer.toString();
    }

    public <T> T requestGetAK(String strUrl, Map<String, String> param, Type typeOfT) throws Exception {
        return gson.fromJson(requestGetAK(strUrl,param),typeOfT);
    }


    public String requestCityWeather(Map<String, String> param)throws Exception{
        return requestGetAK(CITY_WEATHER_URL,param);
    }

    public JsonObject requestDrawMap(Map<String, String> param)throws Exception{
        return requestGetAK(DRAW_MAP_URL,param,JsonObject.class);
    }

    public String requestRoadCondition(Map<String, String> param)throws Exception{
        return requestGetAK(ROAD_CONDITION_URL,param);
    }

    public JsonObject requestRoutePlanning(String way,Map<String, String> param)throws Exception{
        return requestGetAK(String.format("%s%s?", ROUTE_PLANNING_URL,way),param,JsonObject.class);
    }

    public JsonObject requestRoutePlaceSearch(Map<String, String> param)throws Exception{
        return requestGetAK(PLACE_SEARCH_URL,param,JsonObject.class);
    }

    public String requestLocationMap(Map<String, String> param)throws Exception{
        return requestGetAK(ROUTE_PLANNING_URL,param);
    }

    public JsonObject requestPlaceLocation(String origin,String region)throws Exception{
        Map<String,String> params = new LinkedHashMap<>();
        params.put("query",origin);
        params.put("region",region);
        params.put("output","json");
        params.put("ak",AK);
        return requestGetAK(PLACE_LOCATION_URL,params,JsonObject.class);
    }


    public static void main(String[] args) throws Exception {
        BaiDuApiCaller caller = new BaiDuApiCaller();
        JsonObject result = caller.requestPlaceLocation("杭州师范大学","杭州市");
        System.out.println(result.getAsJsonObject().getAsJsonArray("results").get(0));

    }

}
