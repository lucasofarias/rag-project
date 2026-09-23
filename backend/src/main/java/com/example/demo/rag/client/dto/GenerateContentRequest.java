package com.example.demo.rag.client.dto;

import java.util.List;

public record GenerateContentRequest(List<Content> contents) {
    public static GenerateContentRequest ofText(String text) {
        return new GenerateContentRequest(List.of(Content.fromText(text)));
    }
}
