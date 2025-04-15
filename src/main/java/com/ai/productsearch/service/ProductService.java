package com.ai.productsearch.service;

import com.ai.productsearch.dto.ProductDTO;
import com.ai.productsearch.exception.ProductNotFoundException;
import com.ai.productsearch.model.Product;
import com.ai.productsearch.model.User;
import com.ai.productsearch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setBrand(productDTO.getBrand());
        product.setCreatedBy(user);
        product.setUpdatedBy(user);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return convertToDTO(savedProduct);
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

        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setBrand(productDTO.getBrand());
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return convertToDTO(updatedProduct);
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

    public List<ProductDTO> searchProducts(String query) {
        log.info("Searching products with query: {}", query);
        List<Product> products = productRepository.searchProducts(query);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> findByCategory(String category) {
        log.info("Finding products by category: {}", category);
        List<Product> products = productRepository.findByCategory(category);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Finding products by price range: {} - {}", minPrice, maxPrice);
        
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Both minPrice and maxPrice must be provided");
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getLatestProducts() {
        log.info("Getting latest products");
        List<Product> products = productRepository.findLatestProducts();
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getLatestProductsByCategory(String category) {
        log.info("Getting latest products by category: {}", category);
        List<Product> products = productRepository.findLatestProductsByCategory(category);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeDuplicateProducts() {
        log.info("Starting duplicate product cleanup");
        List<Product> allProducts = productRepository.findAll();
        
        for (Product product : allProducts) {
            List<Product> duplicates = productRepository.findByNameAndCategoryAndBrand(
                product.getName(),
                product.getCategory(),
                product.getBrand(),
                product.getPrice()
            );
            
            if (duplicates.size() > 1) {
                // Keep the first product and delete the rest
                for (int i = 1; i < duplicates.size(); i++) {
                    Product duplicate = duplicates.get(i);
                    productRepository.delete(duplicate);
                    log.info("Deleted duplicate product with ID: {}", duplicate.getId());
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
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
} 
