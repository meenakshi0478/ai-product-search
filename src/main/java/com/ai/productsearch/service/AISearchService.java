package com.ai.productsearch.service;

import com.ai.productsearch.dto.AISearchRequest;
import com.ai.productsearch.dto.ProductDTO;
import com.ai.productsearch.model.Product;
import com.ai.productsearch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AISearchService {

    private final ProductRepository productRepository;
    private final EmbeddingService embeddingService;
    private final VectorDatabaseService vectorDatabaseService;

    public List<ProductDTO> searchProducts(AISearchRequest request) {
        log.info("Processing AI search request: {}", request);
        
        // Generate embedding for the search query
        float[] queryEmbedding = embeddingService.generateEmbedding(request.getQuery());
        
        // Search for similar products using vector database
        List<Long> similarProductIds = vectorDatabaseService.searchSimilarProducts(
                queryEmbedding,
                request.getCategory(),
                request.getMinPrice(),
                request.getMaxPrice(),
                5
        );
        
        // Fetch products from database
        List<Product> products = productRepository.findAllById(similarProductIds);
        
        // Convert to DTOs and sort if needed
        List<ProductDTO> results = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        if (request.getSortBy() != null) {
            results = sortResults(results, request.getSortBy(), request.getSortDirection());
        }
        
        return results;
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
    }
    
    private List<ProductDTO> sortResults(List<ProductDTO> results, String sortBy, String sortDirection) {
        Comparator<ProductDTO> comparator = switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparing(ProductDTO::getPrice);
            case "name" -> Comparator.comparing(ProductDTO::getName);
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        };
        
        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }
        
        return results.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

} 