package com.example.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {
    @GET("/game/list-icon")
    Call<List<String>> doGetListIconApp();

    @POST("/code/verify/{code}")
    Call<CodeResponse> verifyCode(@Path("code") String code);
}
