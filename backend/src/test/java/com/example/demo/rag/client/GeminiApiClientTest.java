package com.example.demo.rag.client;

import com.example.demo.rag.client.exception.GeminiApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

class GeminiApiClientTest {

    @Test
    @DisplayName("Should throw GeminiApiException when API key is not configured for generateEmbedding")
    void shouldThrowExceptionWhenApiKeyMissingForEmbedding() {
        // Test Goal: Verify that missing API key throws GeminiApiException before making an HTTP request.
        // Given: GeminiApiClient initialized with blank API key
        GeminiApiClient client = new GeminiApiClient(RestClient.builder(), "https://example.com", "", "gemini-embedding-001", "gemini-2.5-flash");

        // When & Then: generateEmbedding should throw GeminiApiException
        assertThrows(GeminiApiException.class, () -> client.generateEmbedding("hello world"));
    }

    @Test
    @DisplayName("Should throw GeminiApiException when API key is not configured for generateResponse")
    void shouldThrowExceptionWhenApiKeyMissingForResponse() {
        // Test Goal: Verify that missing API key throws GeminiApiException before making an HTTP request.
        // Given: GeminiApiClient initialized with blank API key
        GeminiApiClient client = new GeminiApiClient(RestClient.builder(), "https://example.com", "", "gemini-embedding-001", "gemini-2.5-flash");

        // When & Then: generateResponse should throw GeminiApiException
        assertThrows(GeminiApiException.class, () -> client.generateResponse("prompt"));
    }
}
