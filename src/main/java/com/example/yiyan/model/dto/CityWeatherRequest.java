package com.example.yiyan.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

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


    @Getter
    @JsonProperty("district_id")
    private String districtId;

    @Getter
    @JsonProperty("data_type")
    private String dataType;

    public CityWeatherRequest(String districtId, String dataType) {
        this.districtId = districtId;
        this.dataType = dataType;
    }


}
