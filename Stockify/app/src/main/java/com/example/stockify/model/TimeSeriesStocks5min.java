package com.example.stockify.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class TimeSeriesStocks5min {
    @SerializedName("Meta Data")
    private AlphaVantageTimeSeriesDailyJsonMetaData metaData;
    @SerializedName("Time Series (5min)")
    private Map<String, AlphaVantageTimeSeriesDailyJsonDaily> daily;

    public AlphaVantageTimeSeriesDailyJsonMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(AlphaVantageTimeSeriesDailyJsonMetaData metaData) {
        this.metaData = metaData;
    }

    public Map<String, AlphaVantageTimeSeriesDailyJsonDaily> getDaily() {
        return daily;
    }

    public void setDaily(Map<String, AlphaVantageTimeSeriesDailyJsonDaily> daily) {
        this.daily = daily;
    }
}

