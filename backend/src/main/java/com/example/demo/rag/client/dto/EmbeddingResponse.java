package com.example.demo.rag.client.dto;

public record EmbeddingResponse(ContentEmbedding embedding) {
    public record ContentEmbedding(float[] values) {}
}
