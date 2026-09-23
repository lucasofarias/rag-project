package com.example.demo.domain.model;

/**
 * Domain Value Object / Entity representing a greeting.
 * Completely decoupled from any external frameworks or infrastructure.
 */
public record Greeting(String message) {

    public Greeting {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Greeting message cannot be null or blank.");
        }
    }
}
