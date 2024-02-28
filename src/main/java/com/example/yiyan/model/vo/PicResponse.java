package com.example.yiyan.model.vo;

public class PicResponse {
    private String url;
    private String prompt;

    public PicResponse(String url, String prompt) {
        this.url = url;
        this.prompt = prompt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}