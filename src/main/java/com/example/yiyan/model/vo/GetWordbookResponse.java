package com.example.yiyan.model.vo;

import java.util.List;

public class GetWordbookResponse {
    private List<String> wordbook;

    public GetWordbookResponse(List<String> wordbook) {
        this.wordbook = wordbook;
    }

    public List<String> getWordbook() {
        return wordbook;
    }

    public void setWordbook(List<String> wordbook) {
        this.wordbook = wordbook;
    }
}