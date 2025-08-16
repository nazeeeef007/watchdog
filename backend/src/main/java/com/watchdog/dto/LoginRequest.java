package com.watchdog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    public @NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email format") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email format") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password cannot be empty") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password cannot be empty") String password) {
        this.password = password;
    }
}