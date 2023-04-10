package com.example.stockify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CryptoGraphListData {
    @SerializedName("data")
    @Expose
    private List<CryptoGraphSingleData> data;

    public List<CryptoGraphSingleData> getData() {
        return data;
    }

    public void setData(List<CryptoGraphSingleData> data) {
        this.data = data;
    }
}
