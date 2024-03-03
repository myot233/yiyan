package com.example.yiyan.baidu;

import com.google.gson.JsonObject;
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

    public String getOriginPoint(BaiDuApiCaller caller) throws Exception {


        if(startPlace == null || startPlace.isEmpty()){
            return "";
        }
        // TODO 地点搜不到 写死
        JsonObject pos = caller.requestPlaceLocation("杭州东火车站", "杭州");
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
        if(endPlace == null || endPlace.isEmpty()){
            return "";
        }
        JsonObject pos = caller.requestPlaceLocation(endPlace, "全国");
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
