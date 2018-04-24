package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class WasteType {
    @SerializedName("id")
    private int typeId;
    @SerializedName("short_code")
    private ShortCodes shortCode;
    @SerializedName("color")
    private TypeColours typeColour;

    public int getTypeId() {
        return typeId;
    }

    public ShortCodes getShortCode() {
        return shortCode;
    }

    public TypeColours getTypeColour() {
        return typeColour;
    }
}
