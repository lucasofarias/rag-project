package com.example.demo.rag.dto;

import java.util.List;

public record RecipeChatResponse(
        String answer,
        List<RecipeMetadata> matchedRecipes
) {
}
