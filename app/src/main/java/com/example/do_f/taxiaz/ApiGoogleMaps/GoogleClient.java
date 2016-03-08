package com.example.do_f.taxiaz.ApiGoogleMaps;

import com.example.do_f.taxiaz.api.ApiService;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by do_f on 29/01/16.
 */
public class GoogleClient {

    //https://maps.googleapis.com/maps/api/distancematrix/json?origins=48.58887,7.74266&destinations=48.54465,7.62729&language=fr-FR&key=AIzaSyA2SvsMpksW8OZISeg9t0CzLAvbHETgFFw
    private static ApiGoogleMaps restClient;
    private static String WEBSERVICE_HOST = " https://maps.googleapis.com/maps/api/distancematrix/";

    static {
        setupRestClient();
    }

    private GoogleClient()
    {

    }

    public static ApiGoogleMaps get()
    {
        return restClient;
    }

    private static void setupRestClient()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEBSERVICE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restClient = retrofit.create(ApiGoogleMaps.class);
    }
}
