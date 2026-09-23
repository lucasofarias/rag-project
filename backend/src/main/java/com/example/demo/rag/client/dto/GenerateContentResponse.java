package com.example.demo.rag.client.dto;

import java.util.List;

public record GenerateContentResponse(List<Candidate> candidates) {
    public record Candidate(Content content) {}

    public String extractFirstText() {
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }
        Candidate first = candidates.get(0);
        if (first.content() == null || first.content().parts() == null || first.content().parts().isEmpty()) {
            return "";
        }
        return first.content().parts().get(0).text();
    }
}
