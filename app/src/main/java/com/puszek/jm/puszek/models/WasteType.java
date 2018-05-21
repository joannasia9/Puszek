package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class WasteType {
    @SerializedName("id")
    private int typeId;
    @SerializedName("short_code")
    private ShortCodes shortCode;
    @SerializedName("color")
    private TypeColours typeColour;
    @SerializedName("scheduled_disposals")
    private String[] scheduledDisposals;

    public int getTypeId() {
        return typeId;
    }

    public ShortCodes getShortCode() {
        return shortCode;
    }

    public TypeColours getTypeColour() {
        return typeColour;
    }

    public String[] getScheduledDisposals() {
        return scheduledDisposals;
    }
}
