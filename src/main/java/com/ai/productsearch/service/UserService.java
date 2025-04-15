package com.ai.productsearch.service;

import com.ai.productsearch.dto.UserDTO;
import com.ai.productsearch.exception.EmailAlreadyExistsException;
import com.ai.productsearch.exception.ResourceNotFoundException;
import com.ai.productsearch.model.User;
import com.ai.productsearch.model.UserRole;
import com.ai.productsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        log.info("Starting user registration for email: {}", userDTO.getEmail());
        
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Registration failed: Email {} already exists", userDTO.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        
        // Set role based on input or default to USER
        if (userDTO.getRole() != null && !userDTO.getRole().isEmpty()) {
            try {
                user.setRole(UserRole.valueOf(userDTO.getRole().toUpperCase()));
                log.debug("Setting user role to: {}", userDTO.getRole());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided: {}. Defaulting to USER", userDTO.getRole());
                user.setRole(UserRole.USER);
            }
        } else {
            user.setRole(UserRole.USER);
            log.debug("No role provided, defaulting to USER");
        }

        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        log.debug("Saving user to database: {}", user);
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} with role: {}", savedUser.getEmail(), savedUser.getRole());
        
        // Verify the user was saved by attempting to retrieve it
        User verifiedUser = userRepository.findByEmail(savedUser.getEmail())
                .orElseThrow(() -> new IllegalStateException("User was not properly saved to the database"));
        log.info("Verified user exists in database with ID: {}", verifiedUser.getId());
        
        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToDTO(user);
    }

    public User findUserByEmail(String email) {
        log.info("Finding user entity by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().name());
        return dto;
    }
} 