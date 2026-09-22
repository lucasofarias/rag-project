package com.example.demo.rag.client.exception;

public class GeminiRateLimitException extends GeminiApiException {
    public GeminiRateLimitException(String message) {
        super(message);
    }

    public GeminiRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
