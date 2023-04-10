package com.example.stockify.retrofit;

import com.example.stockify.model.CryptoData;
import com.example.stockify.model.CryptoGraphListData;
import com.example.stockify.model.CryptoListData;
import com.example.stockify.model.TimeSeriesStocks;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StockService {

    @GET("query?function=TIME_SERIES_INTRADAY&outputsize=full")
    Call<TimeSeriesStocks> getHistory(@Query("symbol") String symbol, @Query("interval") String interval, @Query("apikey") String key);

}
