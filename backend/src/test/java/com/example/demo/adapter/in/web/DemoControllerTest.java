package com.example.demo.adapter.in.web;

import com.example.demo.application.port.in.GetHelloWorldUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoControllerTest {

    private MockMvc mockMvc;
    private GetHelloWorldUseCase useCase;
    private DemoController demoController;

    @BeforeEach
    void setUp() {
        useCase = mock(GetHelloWorldUseCase.class);
        when(useCase.helloWorld()).thenReturn("Hello World!");
        demoController = new DemoController(useCase);
        mockMvc = MockMvcBuilders.standaloneSetup(demoController).build();
    }

    @Test
    void testHelloWorldUnit() {
        var response = demoController.helloWorld();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Hello World!", response.getBody());
    }

    @Test
    void testHelloWorldEndpointWithDemoPrefix() throws Exception {
        mockMvc.perform(get("/demo/helloWorld"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

    @Test
    void testHelloWorldEndpointKebab() throws Exception {
        mockMvc.perform(get("/hello-world"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

    @Test
    void testHelloWorldEndpointWithoutDemoPrefix() throws Exception {
        mockMvc.perform(get("/helloWorld"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }
}
