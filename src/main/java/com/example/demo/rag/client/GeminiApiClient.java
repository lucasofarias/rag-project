package com.example.demo.rag.client;

import com.example.demo.rag.client.dto.Content;
import com.example.demo.rag.client.dto.EmbeddingRequest;
import com.example.demo.rag.client.dto.EmbeddingResponse;
import com.example.demo.rag.client.dto.GenerateContentRequest;
import com.example.demo.rag.client.dto.GenerateContentResponse;
import com.example.demo.rag.client.exception.GeminiApiException;
import com.example.demo.rag.client.exception.GeminiRateLimitException;
import com.example.demo.rag.client.exception.GeminiTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class GeminiApiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String embeddingModel;
    private final String chatModel;

    public GeminiApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${gemini.api.base-url}") String baseUrl,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.embedding-model}") String embeddingModel,
            @Value("${gemini.api.chat-model}") String chatModel
    ) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.embeddingModel = cleanModelName(embeddingModel.trim());
        this.chatModel = cleanModelName(chatModel.trim());

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(10));
        requestFactory.setReadTimeout(Duration.ofSeconds(60));

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultStatusHandler(
                        status -> status.value() == 429,
                        (request, response) -> {
                            String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new GeminiRateLimitException("Gemini API rate limit exceeded (429): " + body);
                        }
                )
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new GeminiApiException("Gemini API error (" + response.getStatusCode() + "): " + body);
                        }
                )
                .build();
    }

    public float[] generateEmbedding(String text) {
        validateApiKey();
        try {
            // gemini-embedding-001 supports outputDimensionality to match 768 vector dimension
            EmbeddingRequest requestPayload = EmbeddingRequest.of("models/" + embeddingModel, text, 768);

            EmbeddingResponse response = restClient.post()
                    .uri("/models/{model}:embedContent?key={apiKey}", embeddingModel, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(EmbeddingResponse.class);

            if (response == null || response.embedding() == null || response.embedding().values() == null) {
                throw new GeminiApiException("Empty embedding response returned by Gemini API");
            }

            return response.embedding().values();
        } catch (ResourceAccessException e) {
            throw new GeminiTimeoutException("Gemini API timeout or network failure during embedding generation: " + e.getMessage(), e);
        } catch (GeminiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new GeminiApiException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }

    public String generateResponse(String prompt) {
        validateApiKey();
        try {
            GenerateContentRequest requestPayload = GenerateContentRequest.ofText(prompt);

            GenerateContentResponse response = restClient.post()
                    .uri("/models/{model}:generateContent?key={apiKey}", chatModel, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(GenerateContentResponse.class);

            if (response == null) {
                throw new GeminiApiException("Empty content response returned by Gemini API");
            }

            return response.extractFirstText();
        } catch (ResourceAccessException e) {
            throw new GeminiTimeoutException("Gemini API timeout or network failure during content generation: " + e.getMessage(), e);
        } catch (GeminiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new GeminiApiException("Failed to generate response: " + e.getMessage(), e);
        }
    }

    private void validateApiKey() {
        if (apiKey.isEmpty()) {
            throw new GeminiApiException("GEMINI_API_KEY is not configured. Please set GEMINI_API_KEY environment variable or property.");
        }
    }

    private static String cleanModelName(String model) {
        if (model.startsWith("models/")) {
            return model.substring("models/".length());
        }
        return model;
    }
}
