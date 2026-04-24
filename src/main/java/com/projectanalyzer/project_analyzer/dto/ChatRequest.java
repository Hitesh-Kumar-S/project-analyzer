package com.projectanalyzer.project_analyzer.dto;

public class ChatRequest {

    private String question;
    private boolean strictMode;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }
}