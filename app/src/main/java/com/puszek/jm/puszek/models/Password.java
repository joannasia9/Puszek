package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class Password {
    @SerializedName("password")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
