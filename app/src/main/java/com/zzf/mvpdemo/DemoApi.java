package com.zzf.mvpdemo;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DemoApi {

    @GET("index")
    Call<DemoBean> getNews(@QueryMap HashMap<String, String> hashMap);
}
