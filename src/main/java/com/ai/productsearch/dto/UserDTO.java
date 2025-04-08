package com.ai.productsearch.dto;

import com.ai.productsearch.model.UserRole;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String role;
} 