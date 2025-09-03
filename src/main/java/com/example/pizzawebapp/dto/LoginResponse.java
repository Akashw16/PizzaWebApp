package com.example.pizzawebapp.dto;

public class LoginResponse {

    private String token;

    // Default constructor
    public LoginResponse() {}

    // Parameterized constructor
    public LoginResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    // Setter
    public void setToken(String token) {
        this.token = token;
    }
}
