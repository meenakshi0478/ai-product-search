package com.ai.productsearch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @Column(name = "account_expiry_date")
    private LocalDateTime accountExpiryDate;

    @Column(name = "credentials_expiry_date")
    private LocalDateTime credentialsExpiryDate;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_login_attempt")
    private LocalDateTime lastLoginAttempt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountExpiryDate == null || LocalDateTime.now().isBefore(accountExpiryDate);
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsExpiryDate == null || LocalDateTime.now().isBefore(credentialsExpiryDate);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        this.lastLoginAttempt = LocalDateTime.now();
        
        // Lock account after 5 failed attempts within 15 minutes
        if (this.failedLoginAttempts >= 5) {
            if (this.lastLoginAttempt != null && 
                this.lastLoginAttempt.plusMinutes(15).isAfter(LocalDateTime.now())) {
                this.accountNonLocked = false;
            } else {
                // Reset counter if last attempt was more than 15 minutes ago
                this.failedLoginAttempts = 1;
            }
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lastLoginAttempt = null;
    }
}
