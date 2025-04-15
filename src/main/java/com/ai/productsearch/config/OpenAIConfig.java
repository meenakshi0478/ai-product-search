package com.ai.productsearch.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {
    
    @Value("${ai.embedding.api-key:}")
    private String apiKey;
    
    @Value("${ai.embedding.timeout-seconds:30}")
    private int timeoutSeconds;
    
    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(apiKey, Duration.ofSeconds(timeoutSeconds));
    }
} 