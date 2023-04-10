package com.example.stockify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class WatchItem implements Parcelable {
    private String symbol;
    private String name;
    private Double previousClose;
    private Double open;
    private Double high;
    private Double low;
    private Double volume;
    private Double change;
    private Type type;

    public WatchItem() {}

    public WatchItem(String symbol, String name, Double previousClose, Double open, Double high, Double low, Double volume, Double change, Type type) {
        this.symbol = symbol;
        this.name = name;
        this.previousClose = previousClose;
        this.open = open;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.change = change;
        this.type = type;
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

    protected WatchItem(Parcel in) {
        this.symbol = in.readString();
        this.name = in.readString();
        this.previousClose = in.readDouble();
        this.open = in.readDouble();
        this.high = in.readDouble();
        this.low = in.readDouble();
        this.volume = in.readDouble();
        this.change = in.readDouble();
        this.type = Type.valueOf(in.readString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchItem watchItem = (WatchItem) o;
        return Objects.equals(symbol, watchItem.symbol) && Objects.equals(name, watchItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WatchItem> CREATOR = new Creator<WatchItem>() {
        @Override
        public WatchItem createFromParcel(Parcel in) {
            return new WatchItem(in);
        }

        @Override
        public WatchItem[] newArray(int size) {
            return new WatchItem[size];
        }
    };


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(symbol);
        dest.writeString(name);
        dest.writeDouble(previousClose);
        dest.writeDouble(open);
        dest.writeDouble(high);
        dest.writeDouble(low);
        dest.writeDouble(volume);
        dest.writeDouble(change);
        dest.writeString(type.name());
    }

    @Override
    public String toString() {
        return "WatchItem{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", previousClose=" + previousClose +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", change=" + change +
                ", type=" + type +
                '}';
    }
}
