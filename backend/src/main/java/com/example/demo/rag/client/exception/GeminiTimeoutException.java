package com.example.demo.rag.client.exception;

public class GeminiTimeoutException extends GeminiApiException {
    public GeminiTimeoutException(String message) {
        super(message);
    }

    public GeminiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
