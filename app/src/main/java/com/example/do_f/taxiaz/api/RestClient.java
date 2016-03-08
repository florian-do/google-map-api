package com.example.do_f.taxiaz.api;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by do_f on 28/01/16.
 */
public class RestClient
{
    private static ApiService restClient;
    private static String WEBSERVICE_HOST = " http://www.az4.ovh/API/v1/";

    static {
        setupRestClient();
    }

    private RestClient()
    {

    }

    public static ApiService get()
    {
        return restClient;
    }

    private static void setupRestClient()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEBSERVICE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restClient = retrofit.create(ApiService.class);
    }
}
