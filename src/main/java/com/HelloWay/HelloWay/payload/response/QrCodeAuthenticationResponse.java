package com.HelloWay.HelloWay.payload.response;

public class QrCodeAuthenticationResponse {

    private String token;

    public QrCodeAuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
