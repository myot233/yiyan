package com.example.yiyan.model.dto;

public class CityWeatherRequest {
    /**
     * 纬度
     */
    float lat;
    /**
     * 经度
     */
    float log;

    String locationName;



    private String districtId;
    private String dataType;

    public CityWeatherRequest() {
    }

    public CityWeatherRequest(String districtId, String dataType) {
        this.districtId = districtId;
        this.dataType = dataType;
    }

    public String getDistrictId() {
        return districtId;
    }



    public String getDataType() {
        return dataType;
    }


}
