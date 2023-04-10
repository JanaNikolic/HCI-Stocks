
package com.example.stockify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CryptoGraphSingleData {

    @SerializedName("priceUsd")
    @Expose
    private String priceUsd;
    @SerializedName("time")
    @Expose
    private Long time;

    public String getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(String priceUsd) {
        this.priceUsd = priceUsd;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}