package com.watchdog.dto;

public class AuthenticationResponse {
    private String token;

    // Default constructor (required for JSON deserialization by Spring/Jackson)
    public AuthenticationResponse() {
    }

    // Constructor with token (useful for building the response)
    public AuthenticationResponse(String token) {
        this.token = token;
    }

    // Getter for the token
    public String getToken() {
        return token;
    }

    // Setter for the token
    public void setToken(String token) {
        this.token = token;
    }

    // --- Optional: Builder Pattern (if you prefer a more fluent way to create instances) ---
    // This is the manual equivalent of what Lombok's @Builder does.
    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    public static class AuthenticationResponseBuilder {
        private String token;

        AuthenticationResponseBuilder() {
            // Private constructor to force use of builder() method
        }

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponse build() {
            return new AuthenticationResponse(token);
        }
    }
}