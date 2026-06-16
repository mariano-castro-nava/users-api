package com.chakray.usersapi.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Credentials for POST /login. The username is the user's tax_id.
 */
public class LoginRequest {

    @NotBlank(message = "username (tax_id) is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;

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
