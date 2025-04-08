package com.ai.productsearch.service;

import com.ai.productsearch.exception.AuthenticationFailedException;
import com.ai.productsearch.exception.UserNotFoundException;
import com.ai.productsearch.model.User;
import com.ai.productsearch.model.UserRole;
import com.ai.productsearch.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public String authenticateUser(String email, String password) {
        log.info("Authenticating user: {}", email);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            log.info("User {} authenticated successfully", email);
            return jwt;
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", email);
            throw new AuthenticationFailedException("Invalid email or password. Please check your credentials and try again.");
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", email, e);
            throw new AuthenticationFailedException("Authentication failed. Please try again later.");
        }
    }

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            throw new AuthenticationFailedException("Authentication required. Please log in to access this resource.");
        }

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationFailedException("User is not authenticated. Please log in again.");
        }

        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        
        if (user == null) {
            throw new UserNotFoundException("User account not found. Please contact support if this issue persists.");
        }
        
        return user;
    }

    public void validateAdminRole(User user) {
        if (user == null) {
            throw new UserNotFoundException("User account not found. Please contact support if this issue persists.");
        }

        if (user.getRole() != UserRole.ADMIN) {
            log.warn("Unauthorized access attempt by non-admin user: {}", user.getEmail());
            throw new AuthenticationFailedException("Access denied. This operation requires administrator privileges.");
        }
    }

    public void validateUserRole(User user) {
        if (user == null) {
            throw new UserNotFoundException("User account not found. Please contact support if this issue persists.");
        }

        if (!user.isEnabled()) {
            throw new AuthenticationFailedException("Your account has been disabled. Please contact support for assistance.");
        }

        if (!user.isAccountNonLocked()) {
            throw new AuthenticationFailedException("Your account has been locked due to multiple failed login attempts. Please contact support to unlock your account.");
        }

        if (!user.isAccountNonExpired()) {
            throw new AuthenticationFailedException("Your account has expired. Please contact support to renew your account.");
        }

        if (!user.isCredentialsNonExpired()) {
            throw new AuthenticationFailedException("Your password has expired. Please reset your password to continue.");
        }
    }
} 