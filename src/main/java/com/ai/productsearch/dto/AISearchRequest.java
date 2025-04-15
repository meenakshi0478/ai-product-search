package com.ai.productsearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AISearchRequest {

    @NotBlank(message = "Search query is required")
    private String query;
    
    private String category;
    
    @PositiveOrZero(message = "Minimum price must be positive or zero")
    private Double minPrice;
    
    @PositiveOrZero(message = "Maximum price must be positive or zero")
    private Double maxPrice;
    
    private String sortBy;

    private String sortDirection;
} 