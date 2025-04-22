package com.ai.productsearch.controller;

import com.ai.productsearch.dto.ProductDTO;
import com.ai.productsearch.model.User;
import com.ai.productsearch.service.AuthService;
import com.ai.productsearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    @PostMapping("/admin/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductDTO productDTO, Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        authService.validateAdminRole(user);
        authService.validateUserRole(user);

        ProductDTO createdProduct = productService.createProduct(productDTO, user);
        log.info("Product created successfully by admin: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "status", "success",
            "message", "Product created successfully",
            "data", createdProduct
        ));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO, Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        authService.validateAdminRole(user);
        authService.validateUserRole(user);

        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        log.info("Product updated successfully by admin: {}", user.getEmail());
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Product updated successfully",
            "data", updatedProduct
        ));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id, Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        authService.validateAdminRole(user);
        authService.validateUserRole(user);

        productService.deleteProduct(id);
        log.info("Product deleted successfully by admin: {}", user.getEmail());
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Product deleted successfully"
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String query,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Searching products with query: {}", query);
        Page<ProductDTO> products = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", products.getContent(),
            "pagination", Map.of(
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "currentPage", products.getNumber(),
                "pageSize", products.getSize(),
                "hasNext", products.hasNext(),
                "hasPrevious", products.hasPrevious()
            )
        ));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable String category,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting products by category: {}", category);
        Page<ProductDTO> products = productService.findByCategory(category, pageable);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", products.getContent(),
            "pagination", Map.of(
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "currentPage", products.getNumber(),
                "pageSize", products.getSize(),
                "hasNext", products.hasNext(),
                "hasPrevious", products.hasPrevious()
            )
        ));
    }

    @GetMapping("/price-range")
    public ResponseEntity<?> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @PageableDefault(sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Getting products by price range: {} - {}", minPrice, maxPrice);
        Page<ProductDTO> products = productService.findByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", products.getContent(),
            "pagination", Map.of(
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "currentPage", products.getNumber(),
                "pageSize", products.getSize(),
                "hasNext", products.hasNext(),
                "hasPrevious", products.hasPrevious()
            )
        ));
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestProducts(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting latest products");
        Page<ProductDTO> products = productService.getLatestProducts(pageable);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", products.getContent(),
            "pagination", Map.of(
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "currentPage", products.getNumber(),
                "pageSize", products.getSize(),
                "hasNext", products.hasNext(),
                "hasPrevious", products.hasPrevious()
            )
        ));
    }

    @GetMapping("/latest/{category}")
    public ResponseEntity<?> getLatestProductsByCategory(
            @PathVariable String category,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting latest products by category: {}", category);
        Page<ProductDTO> products = productService.getLatestProductsByCategory(category, pageable);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", products.getContent(),
            "pagination", Map.of(
                "totalElements", products.getTotalElements(),
                "totalPages", products.getTotalPages(),
                "currentPage", products.getNumber(),
                "pageSize", products.getSize(),
                "hasNext", products.hasNext(),
                "hasPrevious", products.hasPrevious()
            )
        ));
    }

    @DeleteMapping("/admin/cleanup-duplicates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> cleanupDuplicateProducts(Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        authService.validateAdminRole(user);
        authService.validateUserRole(user);
        productService.removeDuplicateProducts();
        log.info("Duplicate products cleaned up successfully by admin: {}", user.getEmail());
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Duplicate products have been removed successfully"
        ));
    }
} 