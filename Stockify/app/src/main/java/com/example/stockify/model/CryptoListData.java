package com.example.stockify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CryptoListData {

    @SerializedName("data")
    @Expose
    private List<Crypto> data;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;

    public List<Crypto> getData() {
        return data;
    }

    public void setData(List<Crypto> data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
