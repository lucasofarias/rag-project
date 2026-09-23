package com.example.demo.rag.service;

import com.example.demo.rag.client.GeminiApiClient;
import com.example.demo.rag.dto.RecipeChatRequest;
import com.example.demo.rag.dto.RecipeChatResponse;
import com.example.demo.rag.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChefRagServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private GeminiApiClient geminiApiClient;

    @InjectMocks
    private ChefRagService chefRagService;

    @Test
    @DisplayName("Should successfully retrieve recipes, format prompt, and return chat response")
    void shouldProcessChatQuestionSuccessfully() {
        // Test Goal: Verify full RAG flow from question embedding to prompt synthesis and response generation.
        // Given: Mocking embedding generation, repository query results, and LLM response
        float[] dummyEmbedding = new float[]{0.1f, 0.2f, 0.3f};
        when(geminiApiClient.generateEmbedding("apple pie")).thenReturn(dummyEmbedding);

        Object[] dummyRow = new Object[]{
                1L,
                "Apple Pie",
                "/Desserts/Pies/",
                8,
                4.8,
                "https://example.com/apple_pie.jpg",
                "Apples, sugar, butter, flour",
                "Mix ingredients and bake at 200C for 45 min",
                0.92
        };
        when(recipeRepository.findSimilarRecipes(anyString(), eq("Desserts"), eq(5)))
                .thenReturn(List.<Object[]>of(dummyRow));

        when(geminiApiClient.generateResponse(anyString()))
                .thenReturn("Para fazer Apple Pie, misture maçãs, açúcar e manteiga e asse por 45 minutos.");

        RecipeChatRequest request = new RecipeChatRequest("apple pie", "Desserts", 5);

        // When: Executing the service method
        RecipeChatResponse response = chefRagService.askChef(request);

        // Then: Verifying the chat response and interactions
        assertNotNull(response);
        assertEquals("Para fazer Apple Pie, misture maçãs, açúcar e manteiga e asse por 45 minutos.", response.answer());
        assertEquals(1, response.matchedRecipes().size());
        assertEquals("Apple Pie", response.matchedRecipes().get(0).recipeName());
        assertEquals(0.92, response.matchedRecipes().get(0).similarityScore());

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(geminiApiClient).generateResponse(promptCaptor.capture());
        String generatedPrompt = promptCaptor.getValue();
        assertTrue(generatedPrompt.contains("Apple Pie"));
        assertTrue(generatedPrompt.contains("apple pie"));
        verify(geminiApiClient, times(1)).generateEmbedding("apple pie");
        verify(recipeRepository, times(1)).findSimilarRecipes(anyString(), eq("Desserts"), eq(5));
    }

    @Test
    @DisplayName("Should handle empty search results gracefully")
    void shouldHandleEmptySearchResults() {
        // Test Goal: Verify that the service handles empty repository results without failing.
        // Given: Empty search results from repository
        float[] dummyEmbedding = new float[]{0.1f, 0.2f};
        when(geminiApiClient.generateEmbedding(anyString())).thenReturn(dummyEmbedding);
        when(recipeRepository.findSimilarRecipes(anyString(), isNull(), eq(5)))
                .thenReturn(Collections.emptyList());
        when(geminiApiClient.generateResponse(anyString()))
                .thenReturn("Não encontrei receitas correspondentes no momento.");

        RecipeChatRequest request = new RecipeChatRequest("unknown food", null);

        // When: Executing the service method
        RecipeChatResponse response = chefRagService.askChef(request);

        // Then: Verifying response with empty matched recipes
        assertNotNull(response);
        assertEquals("Não encontrei receitas correspondentes no momento.", response.answer());
        assertTrue(response.matchedRecipes().isEmpty());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when request is null or question is blank")
    void shouldThrowExceptionWhenQuestionIsBlank() {
        // Test Goal: Verify that invalid input (null or blank question) throws IllegalArgumentException.
        // Given: Invalid request with blank question
        RecipeChatRequest blankRequest = new RecipeChatRequest("   ", "Desserts");

        // When & Then: Executing askChef throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> chefRagService.askChef(null));
        assertThrows(IllegalArgumentException.class, () -> chefRagService.askChef(blankRequest));
    }
}
