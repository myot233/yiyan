package com.example.yiyan.model.dto;

public class LocationMapRequest {

    /**
     * 纬度
     */
    private String lat;
    /**
     * 经度
     */
    private String log;

    public String getCenter() {
        String center = lat+","+log;
        return center;
    }

}