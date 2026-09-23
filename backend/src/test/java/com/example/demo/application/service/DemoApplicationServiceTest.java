package com.example.demo.application.service;

import com.example.demo.application.port.out.GreetingOutputPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DemoApplicationServiceTest {

    @Test
    void shouldReturnHelloWorldWithoutOutputPort() {
        DemoApplicationService service = new DemoApplicationService();
        String result = service.helloWorld();
        assertEquals("Hello World!", result);
    }

    @Test
    void shouldReturnHelloWorldAndInvokeOutputPort() {
        GreetingOutputPort outputPort = mock(GreetingOutputPort.class);
        DemoApplicationService service = new DemoApplicationService(outputPort);

        String result = service.helloWorld();

        assertEquals("Hello World!", result);
        verify(outputPort).logGreeting(any());
    }
}
