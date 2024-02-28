package com.example.yiyan.model.dto;

import lombok.Data;

@Data
public class RoutePlanningRequest {
    String way;
    float originLat;
    float originLng;

    float destinationLat;
    float destinationLng;
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

    public String getOriginPoint() {
        return originLat + "," + originLng;
    }

    public String getDestinationPoint() {
        return destinationLat + "," + destinationLng;
    }

    public String getWay() {
        String[] ways = {"driving","riding","walking","transit"};
        return ways[Integer.parseInt(way)];
    }
}
