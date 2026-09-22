package com.example.demo.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GreetingTest {

    @Test
    void shouldCreateGreetingWithValidMessage() {
        Greeting greeting = new Greeting("Hello World!");
        assertEquals("Hello World!", greeting.message());
    }

    @Test
    void shouldThrowExceptionWhenMessageIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Greeting(null));
    }

    @Test
    void shouldThrowExceptionWhenMessageIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Greeting("   "));
    }
}
