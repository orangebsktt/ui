package com.hackathon.fastshop.dto;

public class IntentDto {
    private String intentId;
    private String data;

    public IntentDto(){};

    public IntentDto(String intentId, String data) {
        this.intentId = intentId;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIntentId() {
        return intentId;
    }

    public void setIntentId(String intentId) {
        this.intentId = intentId;
    }
}
