package com.ai.productsearch.service;

import com.ai.productsearch.exception.EmbeddingGenerationException;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final OpenAiService openAiService;
    
    @Value("${ai.embedding.model:text-embedding-ada-002}")
    private String model;
    
    @Value("${ai.embedding.dimensions:1536}")
    private int dimensions;
    
    private final ConcurrentHashMap<String, float[]> embeddingCache = new ConcurrentHashMap<>();
    
    @Cacheable(value = "embeddings", key = "#text")
    public float[] generateEmbedding(String text) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException("Text cannot be empty");
        }
        
        // Check cache first
        float[] cachedEmbedding = embeddingCache.get(text);
        if (cachedEmbedding != null) {
            log.debug("Retrieved embedding from cache for text: {}", text);
            return cachedEmbedding;
        }
        
        try {
            log.debug("Generating embedding for text: {}", text);
            EmbeddingRequest request = EmbeddingRequest.builder()
                    .model(model)
                    .input(List.of(text))
                    .build();
            
            List<Embedding> embeddings = openAiService.createEmbeddings(request).getData();
            if (embeddings.isEmpty()) {
                throw new EmbeddingGenerationException("No embeddings generated for text: " + text);
            }
            
            float[] embedding = embeddings.get(0).getEmbedding().stream()
                    .mapToDouble(Double::doubleValue)
                    .collect(() -> new float[dimensions],
                            (array, value) -> array[array.length - 1] = (float) value,
                            (a1, a2) -> {});
            
            // Cache the result
            embeddingCache.put(text, embedding);
            log.debug("Successfully generated and cached embedding for text: {}", text);
            
            return embedding;
        } catch (Exception e) {
            log.error("Error generating embedding for text '{}': {}", text, e.getMessage());
            throw new EmbeddingGenerationException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }
    
    public void clearCache() {
        log.info("Clearing embedding cache");
        embeddingCache.clear();
    }
}
