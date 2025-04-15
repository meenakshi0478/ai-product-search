package com.ai.productsearch.controller;

import com.ai.productsearch.dto.AISearchRequest;
import com.ai.productsearch.dto.ProductDTO;
import com.ai.productsearch.service.AISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AISearchController {

    private final AISearchService aiSearchService;

    @PostMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestBody AISearchRequest request) {
        log.info("Received AI search request: {}", request);

        try {
            List<ProductDTO> products = aiSearchService.searchProducts(request);
            
            if (products.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "status", "info",
                    "message", "No products found matching your search criteria",
                    "query", request.getQuery()
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Search completed successfully",
                "data", products
            ));
        } catch (Exception e) {
            log.error("Error performing AI search", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An error occurred while processing your search request"
            ));
        }
    }
} 