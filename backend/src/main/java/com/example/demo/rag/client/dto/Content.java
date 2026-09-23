package com.example.demo.rag.client.dto;

import java.util.List;

public record Content(List<Part> parts) {
    public static Content fromText(String text) {
        return new Content(List.of(new Part(text)));
    }
}
