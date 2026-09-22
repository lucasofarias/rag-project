package com.example.demo.rag.repository;

import com.example.demo.rag.model.Recipe;
import com.example.demo.rag.util.VectorUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecipeRepositoryIntegrationTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("Should persist and retrieve Recipe with pgvector column transformer")
    void shouldSaveAndQueryRecipeWithEmbedding() {
        // Given
        float[] vector = new float[768];
        vector[0] = 0.5f;
        vector[1] = 0.5f;
        String vectorString = VectorUtils.toVectorString(vector);

        Recipe recipe = Recipe.builder()
                .recipeName("Integration Test Recipe")
                .cuisinePath("Test Cuisine")
                .servings(4)
                .rating(5.0)
                .ingredients("Ingredient A, Ingredient B")
                .directions("Mix and cook")
                .searchContent("Recipe: Test\nCategory: Test\nIngredients: A, B\nDirections: Cook")
                .embedding(vectorString)
                .build();

        // When: Save recipe with vector embedding via JPA
        Recipe saved = recipeRepository.save(recipe);

        // Then: Entity is saved and embedding is preserved
        assertNotNull(saved.getId());
        assertNotNull(saved.getEmbedding());
        assertTrue(saved.getEmbedding().startsWith("["));

        // Query top-K similar recipes with native pgvector query
        List<Object[]> results = recipeRepository.findSimilarRecipes(vectorString, "Test", 5);
        assertFalse(results.isEmpty());
        assertEquals("Integration Test Recipe", results.getFirst()[1]);

        // Cleanup
        recipeRepository.delete(saved);
    }
}
