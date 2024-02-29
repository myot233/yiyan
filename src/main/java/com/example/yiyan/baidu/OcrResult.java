package com.example.yiyan.baidu;

import lombok.Data;

@Data
public class OcrResult {
    private String startTime;
    private String endTime;
    private String startPlace;
    private String endPlace;

    public OcrResult(String startTime,String endTime,String startPlace,String endPlace){
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.startTime = startTime;
        this.endTime = endTime;
    }


}
