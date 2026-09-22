package com.example.demo.rag.seeder;

import com.example.demo.rag.client.GeminiApiClient;
import com.example.demo.rag.model.Recipe;
import com.example.demo.rag.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvDataSeederTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private GeminiApiClient geminiApiClient;

    @Test
    @DisplayName("Should skip ingestion when repository already contains recipes")
    void shouldSkipWhenRepositoryAlreadyHasData() {
        // Test Goal: Verify that seeder does not perform ingestion if the recipe table already contains records.
        // Given: Repository returns count > 0
        when(recipeRepository.count()).thenReturn(10L);
        CsvDataSeeder seeder = new CsvDataSeeder(recipeRepository, geminiApiClient, "recipes.csv", 10, 0, "test-api-key");

        // When: Running seeder
        seeder.run();

        // Then: No interactions with Gemini or save
        verify(recipeRepository, times(1)).count();
        verifyNoInteractions(geminiApiClient);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    @DisplayName("Should skip ingestion when API key is not configured")
    void shouldSkipWhenApiKeyIsMissing() {
        // Test Goal: Verify that seeder skips gracefully when Gemini API key is blank.
        // Given: Repository is empty, but API key is empty
        when(recipeRepository.count()).thenReturn(0L);
        CsvDataSeeder seeder = new CsvDataSeeder(recipeRepository, geminiApiClient, "recipes.csv", 10, 0, "");

        // When: Running seeder
        seeder.run();

        // Then: Seeder does not call Gemini or save
        verifyNoInteractions(geminiApiClient);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    @DisplayName("Should parse CSV and persist recipes up to batch limit")
    void shouldParseCsvAndPersistRecipes(@TempDir Path tempDir) throws IOException {
        // Test Goal: Verify that valid CSV file is parsed, embeddings generated, and recipes saved up to batch limit.
        // Given: Temporary CSV file with 2 recipes
        Path csvFile = tempDir.resolve("test_recipes.csv");
        String csvContent = """
                ,recipe_name,prep_time,cook_time,total_time,servings,yield,ingredients,directions,rating,url,cuisine_path,nutrition,timing,img_src
                0,Pecan Pie,15 mins,1 hrs,1 hrs 15 mins,8,1 pie,"pecans, eggs, sugar, butter","Mix and bake",4.9,http://example.com,/Desserts/Pies/,nutrition,timing,http://example.com/pie.jpg
                1,Apple Crumble,10 mins,30 mins,40 mins,4,4 servings,"apples, flour, butter","Bake crumble",4.5,http://example.com,/Desserts/Fruit/,nutrition,timing,http://example.com/apple.jpg
                """;
        Files.writeString(csvFile, csvContent);

        when(recipeRepository.count()).thenReturn(0L);
        when(geminiApiClient.generateEmbedding(anyString())).thenReturn(new float[]{0.1f, 0.2f});

        CsvDataSeeder seeder = new CsvDataSeeder(recipeRepository, geminiApiClient, csvFile.toString(), 1, 0, "test-key");

        // When: Running seeder with batch-limit = 1
        seeder.run();

        // Then: Only 1 recipe is processed due to batch limit
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository, times(1)).save(recipeCaptor.capture());

        Recipe savedRecipe = recipeCaptor.getValue();
        assertEquals("Pecan Pie", savedRecipe.getRecipeName());
        assertEquals("/Desserts/Pies/", savedRecipe.getCuisinePath());
        assertEquals(8, savedRecipe.getServings());
        assertEquals(4.9, savedRecipe.getRating());
        assertEquals("[0.1,0.2]", savedRecipe.getEmbedding());
    }
}
