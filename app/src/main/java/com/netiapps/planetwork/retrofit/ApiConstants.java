package com.netiapps.planetwork.retrofit;

import com.netiapps.planetwork.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConstants {

    //==== Base Url
    public static String BASE_URL = "https://spindlier-eliminato.000webhostapp.com/";

    //==== End point
    public static final String ENDPOINT = "planetwork/";

    //===== Retrofit Client
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
