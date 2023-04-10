package com.example.stockify.retrofit;

import com.example.stockify.model.CryptoData;
import com.example.stockify.model.CryptoGraphListData;
import com.example.stockify.model.CryptoListData;

import java.sql.Timestamp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CryptoService {

    @GET("assets")
    Call<CryptoListData> getAll();

    @GET("assets/{id}")
    Call<CryptoData> getOneById(@Path("id") String id);

    @GET("assets/{id}/history")
    Call<CryptoGraphListData> getHistory(@Path("id") String id, @Query("interval") String interval, @Query("start") long start, @Query("end") long end);
}
