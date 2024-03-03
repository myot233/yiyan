package com.example.yiyan.baidu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OcrImpl implements OcrInterFace {
    private final Gson gson = new Gson();
    private final static String ApiKey = "uyuJcts5qbha9VwzSDqjuATL";
    private final static String Secret = "S9QpENWaQMsXdSMUShCWDMruu7G1RyEB";

    private final String TIME_REGEX = "\\d?\\d([:：])\\d\\d";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();


    private static String getFileContentAsBase64(Path path, boolean urlEncode) throws IOException {
        byte[] b = Files.readAllBytes(path);
        String base64 = Base64.getEncoder().encodeToString(b);
        if (urlEncode) {
            base64 = URLEncoder.encode(base64, "utf-8");
        }
        return base64;
    }

    private String requestTicketOcr(Path path) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = RequestBody.create(mediaType, String.format("image=%s", getFileContentAsBase64(path, true)));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/multiple_invoice?access_token=" + requestAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body() != null ? response.body().string() : null;
    }

    private String requestWordOcr(Path path) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, String.format("image=%s", getFileContentAsBase64(path, true)));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/accurate?access_token=" + requestAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body().string();

    }

    private String requestAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(String.format("https://aip.baidubce.com/oauth/2.0/token?client_id=%s&client_secret=%s&grant_type=client_credentials", ApiKey, Secret))
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return gson.fromJson(response.body().string(), JsonObject.class).get("access_token").getAsString();
    }

    @Getter
    private static class Location {

        private int top;
        private int left;
        private int width;
        private int height;


    }

    @Getter
    private static class Word {
        private String words;
        private Location location;
    }

    /*
    实现该函数的两种思路
    1.使用可以识别具体位置的ocr识别图片,然后通过坐标之间的关系找到两个时间和始发站中点赞
    2.用ocr得到文字信息,再让文心一言处理后进行路线规划
     */
    @Override
    public OcrResult getPlaceFromImage(Path path) throws IOException {
        List<Word> locationList = new ArrayList<>();

        JsonObject result = gson.fromJson(requestWordOcr(path), JsonObject.class);
        System.out.println(result);
        result.getAsJsonArray("words_result").forEach(jsonElement -> {
            locationList.add(gson.fromJson(jsonElement, Word.class));
        });
        List<Word> timeList = locationList.stream().filter(location -> Pattern.matches(TIME_REGEX, location.words)).collect(Collectors.toList());
        if (timeList.size() < 2) return null;
        int left = timeList.get(0).location.left;
        int left2 = timeList.get(1).location.left;
        int width2 = timeList.get(1).location.width;
        List<Word> keywordList = locationList.stream()
                .filter(location -> location.words.contains("订单号") ||
                        location.words.contains("当日有效"))
                .collect(Collectors.toList());
        List<Word> resultList = locationList.stream()
                .filter(
                        location -> location.location.top < timeList.get(0).location.top &&
                                location.location.top > keywordList.get(0).location.top
                )
                .filter(location -> Math.abs(
                        location.location.left - left) < 5 ||
                        Math.abs(location.location.left + location.location.width - left2 - width2) < 5)
                .collect(Collectors.toList());

        return new OcrResult(
                timeList.get(0).words,
                timeList.get(1).words,
                resultList.get(0).words,
                resultList.get(1).words
        );
    }


    @Override
    public OcrResult getPlaceFromTicket(Path path) throws IOException {
        JsonObject result = gson.fromJson(requestTicketOcr(path), JsonObject.class);
        int words_result_num = result.get("words_result_num").getAsInt();
        if (words_result_num == 0) {
            return null;
        }
        JsonObject first = result.getAsJsonArray("words_result").get(0).getAsJsonObject();
        if (first.get("type").getAsString().equals("others")) {
            return null;
        }

        if (first.get("type").getAsString().equals("train_ticket")) {
            JsonObject wordResult = first.getAsJsonObject("result");
            return new OcrResult(
                    getVal(wordResult, "date") + getVal(wordResult, "time"),
                    null, //ocr 识别车票无法获取终止时间
                    getVal(wordResult, "starting_station"),//wordResult.get("starting_station").getAsString(),
                    getVal(wordResult, "destination_station")//wordResult.get("destination_station").getAsString()
            );
        }
        if (first.get("type").getAsString().equals("bus_ticket")) {
            JsonObject wordResult = first.getAsJsonObject("result");
            String date = getVal(wordResult, "Date");
            String time = getVal(wordResult, "Time");
            String startingStation = getVal(wordResult, "StartingStation");
            String destinationStation = getVal(wordResult, "DestinationStation");

            // Replace null values with "/"
            date = (date != null) ? date : "/";
            time = (time != null) ? time : "/";
            startingStation = (startingStation != null) ? startingStation : "/";
            destinationStation = (destinationStation != null) ? destinationStation : "/";

            return new OcrResult(date + time, "/", startingStation, destinationStation);
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
        JsonArray jsonArray = wordResult.getAsJsonArray(key);
        if (jsonArray != null && jsonArray.size() > 0) {
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            JsonElement wordElement = jsonObject.get("word");
            if (wordElement != null) {
                return wordElement.getAsString();
            }
        }
        return "/";
    }

}
