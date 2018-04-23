package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class RegisteredUser {
    @SerializedName("id")
    private String id;
    @SerializedName("login")
    private String login;
    @SerializedName("email")
    private String email;
    @SerializedName("district")
    private String district;
    @SerializedName("street")
    private String street;
    @SerializedName("number")
    private String houseNumber;

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getDistrict() {
        return district;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }
}
