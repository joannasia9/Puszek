package com.puszek.jm.puszek.models;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("/api/v1/users/")
    Call<User> createUser(@Body User user);

    @GET("/auth/user")
    Call<String> doGetUserList(@Query("user") String user);

}
