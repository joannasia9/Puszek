package com.puszek.jm.puszek.models;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    @POST("/api/v1/users/")
    Call<RegisteredUser> createUser(@Body User user);

    @POST("/api/v1/auth/token/")
    Call<AuthenticationRequestResult> authenticate(@Body AuthenticationRequest request);

    @GET("/api/v1/barcodes/{code}/")
    Call<RequestedBarcodeData> getBarcodeData(@Path("code") String barcodeCode,
                                              @Header("Authorization") String accessToken);

    @GET("/api/v1/waste-types/")
    Call<WasteType[]> getWasteTypes(@Header("Authorization") String accessToken);

    @GET("/api/v1/users/current/")
    Call<RegisteredUser> getCurrentUser(@Header("Authorization") String accessToken);

    @PUT("/api/v1/users/current/")
    Call<RegisteredUser> updateUser(@Header("Authorization") String accessToken,
                                    @Body User user);

    @PUT("/api/v1/users/current/change-password/")
    Call<RegisteredUser> updatePassword(@Header("Authorization") String accessToken,
                                        @Body Password password);

    @POST("/api/v1/barcodes/")
    Call<RequestedBarcodeData> addBarcode(@Body BarcodeToAdd barcode);

}




