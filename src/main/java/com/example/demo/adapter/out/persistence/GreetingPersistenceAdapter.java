package com.example.demo.adapter.out.persistence;

import com.example.demo.application.port.out.GreetingOutputPort;
import com.example.demo.domain.model.Greeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Outbound Adapter (Driven Adapter).
 * Implements the GreetingOutputPort interface to handle outgoing data/events.
 */
@Component
public class GreetingPersistenceAdapter implements GreetingOutputPort {

    private static final Logger log = LoggerFactory.getLogger(GreetingPersistenceAdapter.class);

    @Override
    public void logGreeting(Greeting greeting) {
        log.info("[Outbound Adapter] Processing output for greeting: {}", greeting.message());
    }
}
