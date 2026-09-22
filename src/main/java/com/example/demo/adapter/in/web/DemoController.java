package com.example.demo.adapter.in.web;

import com.example.demo.application.port.in.GetHelloWorldUseCase;
import com.example.demo.application.service.DemoApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound Adapter (Driving Adapter).
 * Exposes REST endpoints and delegates execution to the inbound port (GetHelloWorldUseCase),
 * keeping the controller completely decoupled from concrete business logic implementations.
 */
@RestController
@RequestMapping(path = {"/demo", ""})
public class DemoController {

    private final GetHelloWorldUseCase getHelloWorldUseCase;

    public DemoController() {
        this.getHelloWorldUseCase = new DemoApplicationService();
    }

    @Autowired
    public DemoController(GetHelloWorldUseCase getHelloWorldUseCase) {
        this.getHelloWorldUseCase = getHelloWorldUseCase;
    }

    @GetMapping(path = {"/helloWorld", "/hello-world", "/hello"})
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok(getHelloWorldUseCase.helloWorld());
    }
}
