package com.watchdog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.watchdog.security.JwtService;
import com.watchdog.security.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter: Starting filter chain for URI: " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JwtAuthenticationFilter: No valid JWT token found in Authorization header. Proceeding without authentication.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // "Bearer " is 7 characters
        System.out.println("JwtAuthenticationFilter: JWT token extracted: " + jwt);
        // Note: Renamed method to extractUserEmail for clarity
        userEmail = jwtService.extractUserEmail(jwt); // Extract email from JWT

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("JwtAuthenticationFilter: User email extracted from token: " + userEmail);

            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                System.out.println("JwtAuthenticationFilter: UserDetails loaded for email: " + userEmail);

                // --- NEW DEBUG LOG ---
                if (userDetails instanceof CustomUserDetails) {
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                    System.out.println("JwtAuthenticationFilter: User ID from loaded UserDetails: " + customUserDetails.getId());
                }
                // --- END OF NEW DEBUG LOG ---

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("JwtAuthenticationFilter: Token is valid. Authenticating user.");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credentials are not stored in the token for stateless authentication
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("JwtAuthenticationFilter: Authentication successful. SecurityContextHolder updated.");
                } else {
                    System.out.println("JwtAuthenticationFilter: Token is invalid. Not authenticating.");
                }
            } catch (Exception e) {
                System.err.println("JwtAuthenticationFilter: Error during token validation or user loading: " + e.getMessage());
                // Handle potential exceptions like UsernameNotFoundException
            }
        }
        filterChain.doFilter(request, response);
    }
}
