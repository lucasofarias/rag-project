package com.example.demo.application.port.out;

import com.example.demo.domain.model.Greeting;

/**
 * Outbound Port (Driven Port).
 * Defines the contract for communicating with external downstream systems (persistence, messaging, audit).
 */
public interface GreetingOutputPort {

    void logGreeting(Greeting greeting);
}
