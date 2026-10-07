package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponse {

    @Schema(
            description = "JWT authentication token",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String token;

    @Schema(description = "Authenticated user information")
    private UserResponse user;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            UserResponse user) {

        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}