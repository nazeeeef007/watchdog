package com.watchdog.service;

import com.watchdog.entity.User;
import com.watchdog.repository.UserRepository;
import com.watchdog.dto.UserRegistrationRequest;
import com.watchdog.dto.LoginRequest;
import com.watchdog.dto.AuthenticationResponse; // New DTO
import com.watchdog.security.CustomUserDetails; // For creating userDetails for JWT
import com.watchdog.security.JwtService; // For JWT generation
import com.watchdog.exception.UserAlreadyExistsException;
import com.watchdog.exception.InvalidCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // Inject JwtService
    private final AuthenticationManager authenticationManager; // Inject AuthenticationManager

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthenticationResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Generate JWT upon successful registration
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    // Changed return type to AuthenticationResponse
    public AuthenticationResponse loginUser(LoginRequest request) {
        // This line attempts to authenticate the user.
        // If credentials are bad, an AuthenticationException (e.g., BadCredentialsException) is thrown.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), // Authenticate by email
                        request.getPassword()
                )
        );

        // If authentication is successful (no exception thrown), get UserDetails and generate JWT
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // Get the authenticated UserDetails
        String jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    // You can keep these for internal service use if needed, but not exposed via controller anymore
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}