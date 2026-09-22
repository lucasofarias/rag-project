package com.example.demo.rag.dto;

public record RecipeMetadata(
        Long id,
        String recipeName,
        String cuisinePath,
        Integer servings,
        Double rating,
        String imgSrc,
        Double similarityScore
) {
}
