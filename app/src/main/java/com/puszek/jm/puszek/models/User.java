package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("login")
    private String login;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("district")
    private String district;
    @SerializedName("street")
    private String street;
    @SerializedName("number")
    private String houseNumber;

    public User(String login, String email, String password, String district, String street, String number){
        this.login = login;
        this.email = email;
        this.password = password;
        this.district = district;
        this.street = street;
        this.houseNumber = number;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
