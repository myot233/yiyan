package com.example.yiyan.controller;

import com.example.yiyan.baidu.BaiDuApiCaller;
import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class ImageRequest {
    private String url;
    private String userinput;
    private String startPos;
    private String endPos;
    private String  way;


    public String getWay() {
        way = "1";
        String[] ways = {"driving","riding","walking","transit"};
        return ways[Integer.parseInt(way)];
    }
    public String getOriginPoint(BaiDuApiCaller caller) throws Exception {
        startPos = "杭州师范大学";

        if(startPos == null || startPos.isEmpty()){
            return "";
        }
        JsonObject pos = caller.requestPlaceLocation(startPos, "全国");
        JsonObject location = pos.getAsJsonObject()
                .getAsJsonArray("results")
                .get(0)
                .getAsJsonObject()
                .get("location")
                .getAsJsonObject();
        float originLat = location.get("lat").getAsFloat();
        float originLng = location.get("lng").getAsFloat();
        return originLat + "," + originLng;
    }

    public String getDestinationPoint(BaiDuApiCaller caller) throws Exception {
        if(endPos == null || endPos.isEmpty()){
            return "";
        }
        JsonObject pos = caller.requestPlaceLocation(endPos, "全国");
        JsonObject location = pos.getAsJsonObject()
                .getAsJsonArray("results")
                .get(0)
                .getAsJsonObject()
                .get("location")
                .getAsJsonObject();
        float destinationLat = location.get("lat").getAsFloat();
        float destinationLng = location.get("lng").getAsFloat();
        return destinationLat + "," + destinationLng;
    }
}
