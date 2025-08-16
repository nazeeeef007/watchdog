package com.watchdog.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class JwtLogoutHandler implements LogoutHandler {

    // For a stateless JWT, logout primarily means the client discards the token.
    // If you need server-side token invalidation (e.g., for short-lived tokens or compromised tokens),
    // you would implement a blacklist mechanism (e.g., storing invalidated tokens in Redis with an expiration).
    // For now, this just logs the logout and clears the security context.

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);

        // Here, you could add logic to blacklist the 'jwt' if you had a blacklist service.
        System.out.println("User logged out (JWT discarded by client). Token prefix: " + jwt.substring(0, Math.min(jwt.length(), 20)) + "...");
    }
}