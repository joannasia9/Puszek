package com.puszek.jm.puszek.models;

import com.google.gson.annotations.SerializedName;

public class AuthenticationRequest {

    @SerializedName("grant_type")
    private String grantType = "password";

    @SerializedName("client_id")
    private String clientId = "marK8idxuVXBseopBFK6WT3ldM58hAlbaILupYAZ";

    @SerializedName("client_secret")
    private String clientSecret = "d7nkN4rjGEmboAcfEDHL9rRqAifcisIg8ZXl0vIbGSBGM7IhbBwOnei76tX8lHlqBKMOa1qNsVh7rn0FWdo6wn0RSUeSXdbzKUDTMLO0MZcquCSmdIGoJiH5nSqBa2zc";

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;


    public AuthenticationRequest(String login, String password){
        this.username = login;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
