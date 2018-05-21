package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class BarcodeToAdd {
    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String productName;
    @SerializedName("waste_type")
    private int wasteType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getWasteType() {
        return wasteType;
    }

    public void setWasteType(int wasteType) {
        this.wasteType = wasteType;
    }
}
