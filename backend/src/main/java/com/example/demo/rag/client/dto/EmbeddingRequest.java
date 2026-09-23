package com.example.demo.rag.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmbeddingRequest(String model, Content content, Integer outputDimensionality) {
    public static EmbeddingRequest of(String model, String text) {
        return new EmbeddingRequest(model, Content.fromText(text), null);
    }

    public static EmbeddingRequest of(String model, String text, Integer outputDimensionality) {
        return new EmbeddingRequest(model, Content.fromText(text), outputDimensionality);
    }
}
