package com.watchdog.controller;

import com.watchdog.dto.LoginRequest;
import com.watchdog.dto.AuthenticationResponse; // New DTO import
import com.watchdog.dto.UserRegistrationRequest;
import com.watchdog.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        AuthenticationResponse response = authService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        AuthenticationResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    // Logout endpoint is handled by Spring Security's filter chain as configured in SecurityConfig
    // No explicit controller method needed here unless you have specific server-side logout logic (e.g., JWT blacklist).
}