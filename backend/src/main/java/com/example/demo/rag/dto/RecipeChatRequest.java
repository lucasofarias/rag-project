package com.example.demo.rag.dto;

public record RecipeChatRequest(
        String question,
        String cuisineFilter,
        Integer topK
) {
    public RecipeChatRequest(String question, String cuisineFilter) {
        this(question, cuisineFilter, 5);
    }

    public int resolvedTopK() {
        if (topK == null || topK <= 0) {
            return 5;
        }
        return Math.min(topK, 20);
    }
}
