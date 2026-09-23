package com.example.demo.domain.exception;

/**
 * Thrown when a requested domain resource cannot be found.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
