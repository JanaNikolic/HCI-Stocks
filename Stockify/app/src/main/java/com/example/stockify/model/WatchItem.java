package com.example.stockify.model;

public class WatchItem {
    private String symbol;
    private String name;
    private Double previousClose;
    private Double open;
    private Double high;
    private Double low;
    private Double volume;
    private Double change;

    public WatchItem() {}

    public WatchItem(String symbol, String name, Double previousClose, Double open, Double high, Double low, Double volume, Double change) {
        this.symbol = symbol;
        this.name = name;
        this.previousClose = previousClose;
        this.open = open;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.change = change;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }
}
