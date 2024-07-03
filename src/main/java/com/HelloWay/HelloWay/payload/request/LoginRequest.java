package com.HelloWay.HelloWay.payload.request;




public class LoginRequest {
    private String username;

    private String password;

    private String token;

    public LoginRequest(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token=token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setUToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
