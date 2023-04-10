package com.example.stockify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CryptoData {

    @SerializedName("data")
    @Expose
    private Crypto data;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;

    public Crypto getData() {
        return data;
    }

    public void setData(Crypto data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
