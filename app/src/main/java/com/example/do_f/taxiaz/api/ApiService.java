package com.example.do_f.taxiaz.api;

import com.example.do_f.taxiaz.api.json.RegistersResponse;
import com.example.do_f.taxiaz.api.json.TaxiResponse;
import com.example.do_f.taxiaz.api.post.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by do_f on 28/01/16.
 */
public interface ApiService {

    @Headers({
            "Content-Type: application/json",
            "Authorization: Basic dGVzdDpjbGllbnQ="
    })
    @GET("taxis")
    Call<TaxiResponse> getTaxi();

    @Headers({
            "Authorization: Basic dGVzdDpjbGllbnQ="
    })

    @POST("registers")
    Call<RegistersResponse> postCustomer(@Body User user);
    //User(String mail, String phone, String address, Double lng, Double lat)

}
