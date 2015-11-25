package com.govindadasu.androidboilerplate.bo;

import com.google.gson.Gson;

import org.json.JSONObject;
public class SarverAccessTocken {


    private String accessToken;

    private String tokenType;

    private Integer expiresIn;

    private String refreshToken;

    private String scope;


    public static SarverAccessTocken fromJson(String jsonObject)
    {
        if(jsonObject==null) return null;
        Gson gson=new Gson();
       return gson.fromJson(jsonObject.toString(),SarverAccessTocken.class);

    }

    public String getAccessToken() {
        return accessToken;
    }


    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;}

    public String getTokenType() {
        return tokenType;
    }


    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }


    public Integer getExpiresIn() {
        return expiresIn;
    }


    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }



    public String getRefreshToken() {
        return refreshToken;
    }


    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }






}