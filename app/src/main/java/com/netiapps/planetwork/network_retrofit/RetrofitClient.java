package com.netiapps.planetwork.network_retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.netiapps.planetwork.utils.Constants;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private RetrofitApi myApi;

    private RetrofitClient() {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okclient = new OkHttpClient.Builder()
//                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient().disableHtmlEscaping()
                .create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitApi.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public RetrofitApi getMyApi() {
        return myApi;
    }
}
