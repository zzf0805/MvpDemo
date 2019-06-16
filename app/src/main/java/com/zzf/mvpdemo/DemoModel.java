package com.zzf.mvpdemo;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemoModel {


    public static void getNews(HashMap<String,String> map, final RequestCallback requestCallback){
        Call<DemoBean> demoBeanCall = NetUtils.getInstance().getDemoApi().getNews(map);
        demoBeanCall.enqueue(new Callback<DemoBean>() {
            @Override
            public void onResponse(Call<DemoBean> call, Response<DemoBean> response) {
                if(response.body()!=null){
                    Log.d("zzfmodel",response.body().toString());
                    requestCallback.onSuccess(response.body(),"demo");
                }

            }

            @Override
            public void onFailure(Call<DemoBean> call, Throwable throwable) {
                Log.d("zzfmodel",throwable.getMessage());
                requestCallback.onError(throwable.getMessage(),"demo");
            }
        });

    }

}
