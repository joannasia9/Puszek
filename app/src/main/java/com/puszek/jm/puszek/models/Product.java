package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private int productId;
    @SerializedName("name")
    private String productName;
    @SerializedName("type")
    private WasteType wasteType;

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public WasteType getWasteType() {
        return wasteType;
    }
}
