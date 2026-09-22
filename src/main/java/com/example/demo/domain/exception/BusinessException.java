package com.example.demo.domain.exception;

/**
 * Thrown when a business rule or condition is violated.
 */
public class BusinessException extends DomainException {

    public BusinessException(String message) {
        super(message);
    }
}
