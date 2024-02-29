package com.example.yiyan.baidu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class OcrImpl implements OcrInterFace {
    private final Gson gson = new Gson();
    private final static String ApiKey = "uyuJcts5qbha9VwzSDqjuATL";
    private final static String Secret = "S9QpENWaQMsXdSMUShCWDMruu7G1RyEB";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static void main(String []args) throws IOException {
        OcrImpl ocr = new OcrImpl();

        OcrResult result  = ocr.getPlaceFromTicket(Paths.get("下载.png"));
        System.out.println(result);
        OcrResult result2  = ocr.getPlaceFromTicket(Paths.get("E:\\wxyy\\yiyan\\demo2.jpg"));
        System.out.println(result2);
    }

    private static String getFileContentAsBase64(Path path, boolean urlEncode) throws IOException {
        byte[] b = Files.readAllBytes(path);
        String base64 = Base64.getEncoder().encodeToString(b);
        if (urlEncode) {
            base64 = URLEncoder.encode(base64, "utf-8");
        }
        return base64;
    }

    private String requestTicketOcr(Path path) throws IOException{
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = RequestBody.create(mediaType, String.format("image=%s", getFileContentAsBase64(path,true)));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/multiple_invoice?access_token=" + requestAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body() != null ? response.body().string() : null;
    }

    private String requestAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(String.format("https://aip.baidubce.com/oauth/2.0/token?client_id=%s&client_secret=%s&grant_type=client_credentials",ApiKey,Secret))
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return gson.fromJson(response.body().string(), JsonObject.class).get("access_token").getAsString();
    }


    @Override
    public OcrResult getPlaceFromImage(Path path) {
        File file = path.toFile();

        return null;
     }

    @Override
    public OcrResult getPlaceFromTicket(Path path) throws IOException {
        JsonObject result =  gson.fromJson(requestTicketOcr(path),JsonObject.class);
        int words_result_num = result.get("words_result_num").getAsInt();
        if(words_result_num == 0){
            return null;
        }
        JsonObject first = result.getAsJsonArray("words_result").get(0).getAsJsonObject();
        if(first.get("type").getAsString().equals("others")){
            return null;
        }

        if(first.get("type").getAsString().equals("train_ticket")){
            JsonObject wordResult = first.getAsJsonObject("result");
            return new OcrResult(
                    getVal(wordResult,"date") + getVal(wordResult,"time"),
                    null, //ocr 识别车票无法获取终止时间
                    getVal(wordResult,"starting_station"),//wordResult.get("starting_station").getAsString(),
                    getVal(wordResult,"destination_station")//wordResult.get("destination_station").getAsString()
            );
        }







        return null;
//        JsonObject wordResult = result.getAsJsonObject("words_result");
//        return new OcrResult(
//                wordResult.get("date").getAsString() + wordResult.get("time").getAsString(),
//                null, //ocr 识别车票无法获取终止时间
//                wordResult.get("starting_station").getAsString(),
//                wordResult.get("destination_station").getAsString()
//        );
    }

    private static String getVal(JsonObject wordResult, String key) {
        return wordResult.getAsJsonArray(key).get(0).getAsJsonObject().get("word").getAsString();
    }
}
