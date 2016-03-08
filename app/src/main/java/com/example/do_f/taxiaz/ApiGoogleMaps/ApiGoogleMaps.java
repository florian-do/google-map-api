package com.example.do_f.taxiaz.ApiGoogleMaps;

import com.example.do_f.taxiaz.ApiGoogleMaps.json.DistanceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by do_f on 29/01/16.
 */
public interface ApiGoogleMaps {

    //json?origins=48.58887,7.74266&destinations=48.54465,7.62729&language=fr-FR&key=AIzaSyA2SvsMpksW8OZISeg9t0CzLAvbHETgFFw
    @GET("json")
    Call<DistanceResponse> getDistance(
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("language") String language,
            @Query("key") String key
            );

}