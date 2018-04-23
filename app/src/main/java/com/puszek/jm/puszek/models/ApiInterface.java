package com.puszek.jm.puszek.models;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("/api/v1/users/")
    Call<RegisteredUser> createUser(@Body User user);

    @POST("/api/v1/auth/token/")
    Call<AuthenticationRequestResult> authenticate(@Body AuthenticationRequest request);

    @GET("/api/v1/auth/token/")
    Call<String> getBarcode();

}
