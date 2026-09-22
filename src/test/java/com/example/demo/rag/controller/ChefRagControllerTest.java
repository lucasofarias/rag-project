package com.example.demo.rag.controller;

import com.example.demo.rag.dto.RecipeChatRequest;
import com.example.demo.rag.dto.RecipeChatResponse;
import com.example.demo.rag.dto.RecipeMetadata;
import com.example.demo.rag.service.ChefRagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChefRagControllerTest {

    @Mock
    private ChefRagService chefRagService;

    @InjectMocks
    private ChefRagController chefRagController;

    @Test
    @DisplayName("Should return 200 OK with RecipeChatResponse when chat endpoint is called")
    void shouldReturnChatResponseSuccessfully() {
        // Test Goal: Verify that the controller delegates to ChefRagService and returns 200 OK with the response.
        // Given: Mocking ChefRagService to return a valid RecipeChatResponse
        RecipeMetadata metadata = new RecipeMetadata(1L, "Apple Pie", "Desserts", 8, 4.5, "img.jpg", 0.95);
        RecipeChatResponse expectedResponse = new RecipeChatResponse("Here is how to bake apple pie", List.of(metadata));
        when(chefRagService.askChef(any(RecipeChatRequest.class))).thenReturn(expectedResponse);

        RecipeChatRequest request = new RecipeChatRequest("How to bake apple pie?", "Desserts");

        // When: Calling chat endpoint
        ResponseEntity<RecipeChatResponse> responseEntity = chefRagController.chat(request);

        // Then: The status is 200 OK and body matches expected response
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(chefRagService, times(1)).askChef(request);
    }
}
