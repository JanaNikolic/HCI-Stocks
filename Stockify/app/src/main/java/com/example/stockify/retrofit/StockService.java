package com.example.stockify.retrofit;

import com.example.stockify.model.TimeSeriesStocks15min;
import com.example.stockify.model.TimeSeriesStocks1min;
import com.example.stockify.model.TimeSeriesStocks5min;
import com.example.stockify.model.TimeSeriesStocks60min;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockService {

    @GET("query?function=TIME_SERIES_INTRADAY&outputsize=full&interval=1min")
    Call<TimeSeriesStocks1min> getHistoryHour(@Query("symbol") String symbol, @Query("apikey") String key);
    @GET("query?function=TIME_SERIES_INTRADAY&outputsize=full&interval=5min")
    Call<TimeSeriesStocks5min> getHistoryDay(@Query("symbol") String symbol, @Query("apikey") String key);
    @GET("query?function=TIME_SERIES_INTRADAY&outputsize=full&interval=15min")
    Call<TimeSeriesStocks15min> getHistoryWeek(@Query("symbol") String symbol, @Query("apikey") String key);
    @GET("query?function=TIME_SERIES_INTRADAY&outputsize=full&interval=60min")
    Call<TimeSeriesStocks60min> getHistoryMonth(@Query("symbol") String symbol, @Query("apikey") String key);

}
