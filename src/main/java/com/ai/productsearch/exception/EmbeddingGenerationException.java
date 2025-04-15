package com.ai.productsearch.exception;

public class EmbeddingGenerationException extends RuntimeException {
    public EmbeddingGenerationException(String message) {
        super(message);
    }
    
    public EmbeddingGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
} 