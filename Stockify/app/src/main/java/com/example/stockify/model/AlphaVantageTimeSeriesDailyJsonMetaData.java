package com.example.stockify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlphaVantageTimeSeriesDailyJsonMetaData {
    @SerializedName("1. Information")
    @Expose
    private String _1Information;
    @SerializedName("2. Symbol")
    @Expose
    private String _2Symbol;
    @SerializedName("3. LastRefreshed")
    @Expose
    private String _3LastRefreshed;
    @SerializedName("4. Interval")
    @Expose
    private String _4Interval;
    @SerializedName("5. OutputSize")
    @Expose
    private String _5OutputSize;
    @SerializedName("6. TimeZone")
    @Expose
    private String _6TimeZone;

    public String get_1Information() {
        return _1Information;
    }

    public void set_1Information(String _1Information) {
        this._1Information = _1Information;
    }

    public String get_2Symbol() {
        return _2Symbol;
    }

    public void set_2Symbol(String _2Symbol) {
        this._2Symbol = _2Symbol;
    }

    public String get_3LastRefreshed() {
        return _3LastRefreshed;
    }

    public void set_3LastRefreshed(String _3LastRefreshed) {
        this._3LastRefreshed = _3LastRefreshed;
    }

    public String get_4Interval() {
        return _4Interval;
    }

    public void set_4Interval(String _4Interval) {
        this._4Interval = _4Interval;
    }

    public String get_5OutputSize() {
        return _5OutputSize;
    }

    public void set_5OutputSize(String _5OutputSize) {
        this._5OutputSize = _5OutputSize;
    }

    public String get_6TimeZone() {
        return _6TimeZone;
    }

    public void set_6TimeZone(String _6TimeZone) {
        this._6TimeZone = _6TimeZone;
    }
}
