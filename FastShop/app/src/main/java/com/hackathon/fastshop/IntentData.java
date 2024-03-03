package com.hackathon.fastshop;

public class IntentData {
    private String intentId;
    private String data;

    public IntentData(){};

    public IntentData(String intentId, String data) {
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
