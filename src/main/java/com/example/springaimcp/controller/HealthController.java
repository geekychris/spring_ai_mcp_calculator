package com.example.springaimcp.controller;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller providing health check and status endpoints.
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    private final OllamaApi ollamaApi;
    
    @Value("${spring.ai.ollama.model}")
    private String model;

    private OllamaChatClient ollamaChatClient;
    @Autowired
    public HealthController(OllamaApi ollamaApi, OllamaChatClient ollamaChatClient) {
        this.ollamaApi = ollamaApi;
        this.ollamaChatClient = ollamaChatClient;
    }

    /**
     * Simple health check endpoint.
     * 
     * @return basic application status information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Spring AI Calculator");
        
        // Check Ollama connectivity
        Map<String, Object> ollama = new HashMap<>();
        try {
            // Just trigger a simple request to see if Ollama is accessible
            // Simple health check by sending a minimal prompt
            // Create a proper request object for the health check
//            var request = OllamaApi.GenerateRequest.builder("hi")
//                .withModel(model)
//                .build();

            ollamaChatClient.call(new Prompt("hi")).getResult().getOutput().getContent();; // Block to ensure connection is established

            ollama.put("status", "UP");
            ollama.put("model", model);
        } catch (ResourceAccessException e) {
            ollama.put("status", "DOWN");
            ollama.put("error", "Ollama service unavailable. Make sure it's running.");
        } catch (Exception e) {
            ollama.put("status", "ERROR");
            ollama.put("error", e.getMessage());
        }
        
        status.put("ollama", ollama);
        
        return ResponseEntity.ok(status);
    }
}

