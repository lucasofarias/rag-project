package com.example.demo.application.service;

import com.example.demo.application.port.in.GetHelloWorldUseCase;
import com.example.demo.application.port.out.GreetingOutputPort;
import com.example.demo.domain.model.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Application Service (Inside the Hexagon).
 * Implements the inbound port (GetHelloWorldUseCase) and coordinates domain logic,
 * interacting with outbound ports (GreetingOutputPort) when needed.
 */
@Service
public class DemoApplicationService implements GetHelloWorldUseCase {

    private final GreetingOutputPort greetingOutputPort;

    public DemoApplicationService() {
        this.greetingOutputPort = null;
    }

    @Autowired(required = false)
    public DemoApplicationService(GreetingOutputPort greetingOutputPort) {
        this.greetingOutputPort = greetingOutputPort;
    }

    @Override
    public String helloWorld() {
        Greeting greeting = new Greeting("Hello World!");
        if (greetingOutputPort != null) {
            greetingOutputPort.logGreeting(greeting);
        }
        return greeting.message();
    }
}
