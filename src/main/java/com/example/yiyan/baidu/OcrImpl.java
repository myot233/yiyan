package com.example.yiyan.baidu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import springfox.documentation.service.ApiKey;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
        // image 可以通过 getFileContentAsBase64("C:\fakepath\下载.png") 方法获取,如果Content-Type是application/x-www-form-urlencoded时,第二个参数传true
        RequestBody body = RequestBody.create(mediaType, String.format("image=%s", getFileContentAsBase64(path,true)));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/train_ticket?access_token=" + requestAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return  response.body().string();
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
        JsonObject wordResult = result.getAsJsonObject("words_result");
        return new OcrResult(
                wordResult.get("date").getAsString() + wordResult.get("time").getAsString(),
                null, //ocr 识别车票无法获取终止时间
                wordResult.get("starting_station").getAsString(),
                wordResult.get("destination_station").getAsString()
        );
    }
}
