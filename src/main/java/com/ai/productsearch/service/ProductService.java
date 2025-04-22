package com.ai.productsearch.service;

import com.ai.productsearch.dto.ProductDTO;
import com.ai.productsearch.exception.ProductNotFoundException;
import com.ai.productsearch.model.Product;
import com.ai.productsearch.model.User;
import com.ai.productsearch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, User user) {
        log.info("Creating new product: {}", productDTO.getName());
        
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (productDTO.getUpc() == null || productDTO.getUpc().trim().isEmpty()) {
            throw new IllegalArgumentException("UPC code is required");
        }

        // Check if UPC already exists
        if (productRepository.existsByUpc(productDTO.getUpc())) {
            throw new IllegalArgumentException("A product with this UPC code already exists");
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setBrand(productDTO.getBrand());
        product.setUpc(productDTO.getUpc());
        product.setCreatedBy(user);
        product.setUpdatedBy(user);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        try {
            Product savedProduct = productRepository.save(product);
            log.info("Product created successfully with ID: {}", savedProduct.getId());
            return convertToDTO(savedProduct);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("A product with this UPC code already exists");
        }
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        if (productDTO.getName() != null && !productDTO.getName().trim().isEmpty()) {
            product.setName(productDTO.getName());
        }
        
        if (productDTO.getPrice() != null) {
            if (productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Product price must be greater than zero");
            }
            product.setPrice(productDTO.getPrice());
        }

        if (productDTO.getUpc() != null && !productDTO.getUpc().trim().isEmpty()) {
            // Check if the new UPC is different and already exists
            if (!productDTO.getUpc().equals(product.getUpc()) && 
                productRepository.existsByUpc(productDTO.getUpc())) {
                throw new IllegalArgumentException("A product with this UPC code already exists");
            }
            product.setUpc(productDTO.getUpc());
        }

        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setBrand(productDTO.getBrand());
        product.setUpdatedAt(LocalDateTime.now());

        try {
            Product updatedProduct = productRepository.save(product);
            log.info("Product updated successfully with ID: {}", updatedProduct.getId());
            return convertToDTO(updatedProduct);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("A product with this UPC code already exists");
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    public Page<ProductDTO> searchProducts(String query, Pageable pageable) {
        log.info("Searching products with query: {}", query);
        return productRepository.searchProducts(query, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> findByCategory(String category, Pageable pageable) {
        log.info("Finding products by category: {}", category);
        return productRepository.findByCategory(category, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Finding products by price range: {} - {}", minPrice, maxPrice);
        
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Both minPrice and maxPrice must be provided");
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getLatestProducts(Pageable pageable) {
        log.info("Getting latest products");
        return productRepository.findLatestProducts(pageable)
                .map(this::convertToDTO);
    }

    public Page<ProductDTO> getLatestProductsByCategory(String category, Pageable pageable) {
        log.info("Getting latest products by category: {}", category);
        return productRepository.findLatestProductsByCategory(category, pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public void removeDuplicateProducts() {
        log.info("Starting duplicate product cleanup based on UPC");
        List<Product> allProducts = productRepository.findAll();
        
        for (Product product : allProducts) {
            List<Product> duplicates = productRepository.findByUpc(product.getUpc());
            
            if (duplicates.size() > 1) {
                // Keep the oldest product (first in the list due to ORDER BY createdAt ASC)
                Product oldestProduct = duplicates.get(0);
                
                // Delete all other duplicates
                for (int i = 1; i < duplicates.size(); i++) {
                    Product duplicate = duplicates.get(i);
                    productRepository.delete(duplicate);
                    log.info("Deleted duplicate product with ID: {} (UPC: {})", duplicate.getId(), duplicate.getUpc());
                }
            }
        }
        log.info("Duplicate product cleanup completed");
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .brand(product.getBrand())
                .upc(product.getUpc())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
} 
