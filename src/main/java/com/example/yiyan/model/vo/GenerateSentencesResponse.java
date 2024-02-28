package com.example.yiyan.model.vo;

import java.util.List;

public class GenerateSentencesResponse {
    private List<String> words;
    private String prompt;

    public GenerateSentencesResponse(List<String> words, String prompt) {
        this.words = words;
        this.prompt = prompt;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}