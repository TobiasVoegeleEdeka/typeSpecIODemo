package com.example.demo.model;

public class LoginResponse {
    private String token;
    private UserProfile user;

    public LoginResponse() {}
    public LoginResponse(String token, UserProfile user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserProfile getUser() { return user; }
    public void setUser(UserProfile user) { this.user = user; }
}
