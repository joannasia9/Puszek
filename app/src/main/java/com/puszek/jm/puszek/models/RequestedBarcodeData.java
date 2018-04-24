package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class RequestedBarcodeData {
    @SerializedName("id")
    private int id;
    @SerializedName("code")
    private String code;
    @SerializedName("product")
    private Product product;

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Product getProduct() {
        return product;
    }
}
