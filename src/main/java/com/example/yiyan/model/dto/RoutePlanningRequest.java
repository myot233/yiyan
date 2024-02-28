package com.example.yiyan.model.dto;

import com.example.yiyan.baidu.BaiDuApiCaller;
import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class RoutePlanningRequest {
    String way;
    float originLat;
    float originLng;

    float destinationLat;
    float destinationLng;
    String region;
    String origin;
    String destination;
    String originUid;
    String destinationUid;
    /**
     * 车牌号
     */
    String plateNumber;
    /**
     * 途经点
     */
    String waypoints;
    /**
     * 路线偏好
     */
    String tactics;

    public String getOriginPoint(BaiDuApiCaller caller) throws Exception {

        JsonObject pos =  caller.requestPlaceLocation(origin,region);
        JsonObject location =  pos.getAsJsonObject()
                .getAsJsonArray("results")
                .get(0)
                .getAsJsonObject()
                .get("location")
                .getAsJsonObject();
        originLat = location.get("lat").getAsFloat();
        originLng = location.get("lng").getAsFloat();
        return originLat + "," + originLng;
    }

    public String getDestinationPoint(BaiDuApiCaller caller) throws Exception {

        JsonObject pos =  caller.requestPlaceLocation(destination,region);
        JsonObject location =  pos.getAsJsonObject()
                .getAsJsonArray("results")
                .get(0)
                .getAsJsonObject()
                .get("location")
                .getAsJsonObject();
        destinationLat = location.get("lat").getAsFloat();
        destinationLng = location.get("lng").getAsFloat();
        return destinationLat + "," + destinationLng;
    }

    public String getWay() {
        String[] ways = {"driving","riding","walking","transit"};
        return ways[Integer.parseInt(way)];
    }
}
