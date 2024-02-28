package com.example.yiyan.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoadConditionRequest {

    private String roadName;
    private String city;

    public RoadConditionRequest() {
    }

    public RoadConditionRequest(String roadName, String city) {
        this.roadName = roadName;
        this.city = city;
    }

}
