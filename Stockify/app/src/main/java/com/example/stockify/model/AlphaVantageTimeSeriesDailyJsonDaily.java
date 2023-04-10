package com.example.stockify.model;

import com.google.gson.annotations.SerializedName;

//
public class AlphaVantageTimeSeriesDailyJsonDaily {
    @SerializedName("1. open")
    private String openingPrice;
    @SerializedName("2. high")
    private String highPrice;
    @SerializedName("3. low")
    private String lowPrice;
    @SerializedName("4. close")
    private String closingPrice;
    @SerializedName("5. volume")
    private String volume;

    public String getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(String openingPrice) {
        this.openingPrice = openingPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(String closingPrice) {
        this.closingPrice = closingPrice;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
