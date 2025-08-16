package com.watchdog.security;


import com.watchdog.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Since all users are just "users", we assign a default role.
        // Spring Security convention is to prefix roles with "ROLE_".
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash(); // Use your User entity's passwordHash field
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Use email as the username for Spring Security context
    }

    @Override
    public boolean isAccountNonExpired() { return true; } // Implement logic if needed
    @Override
    public boolean isAccountNonLocked() { return true; } // Implement logic if needed
    @Override
    public boolean isCredentialsNonExpired() { return true; } // Implement logic if needed
    @Override
    public boolean isEnabled() { return true; } // Implement logic if needed

    // Expose the User ID and User entity for convenience in controllers/services
    public Long getId() { return user.getId(); }
    public User getUser() { return user; }
}