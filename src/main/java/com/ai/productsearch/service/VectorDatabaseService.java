package com.ai.productsearch.service;

import com.ai.productsearch.model.Product;
import com.ai.productsearch.model.ProductVector;
import com.ai.productsearch.repository.ProductRepository;
import com.ai.productsearch.repository.ProductVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDatabaseService {
    private final ProductRepository productRepository;
    private final ProductVectorRepository productVectorRepository;
    
    @Transactional
    public void saveVector(Long productId, float[] vector) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        ProductVector productVector = ProductVector.builder()
                .product(product)
                .embedding(vector)
                .build();
        
        productVectorRepository.save(productVector);
        log.debug("Saved vector for product: {}", productId);
    }
    
    public List<Long> searchSimilarProducts(float[] queryVector, String category, Double minPrice, Double maxPrice, int limit) {
        // Convert float array to PostgreSQL vector string format
        String vectorString = Arrays.toString(queryVector)
                .replace("[", "{")
                .replace("]", "}");
        
        List<Long> productIds = productVectorRepository.findSimilarProducts(vectorString, limit);
        
        return productIds.stream()
                .filter(productId -> {
                    if (category != null || minPrice != null || maxPrice != null) {
                        Product product = productRepository.findById(productId).orElse(null);
                        if (product == null) return false;
                        if (category != null && !category.equals(product.getCategory())) return false;
                        if (minPrice != null && product.getPrice().doubleValue() < minPrice) return false;
                        if (maxPrice != null && product.getPrice().doubleValue() > maxPrice) return false;
                    }
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Transactional
    public void deleteVector(Long productId) {
        productVectorRepository.deleteByProductId(productId);
        log.debug("Deleted vector for product: {}", productId);
    }
} 