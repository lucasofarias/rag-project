package com.example.demo.rag.controller;

import com.example.demo.rag.dto.RecipeChatRequest;
import com.example.demo.rag.dto.RecipeChatResponse;
import com.example.demo.rag.service.ChefRagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recipes")
public class ChefRagController {

    private final ChefRagService chefRagService;

    public ChefRagController(ChefRagService chefRagService) {
        this.chefRagService = chefRagService;
    }

    @PostMapping("/chat")
    public ResponseEntity<RecipeChatResponse> chat(@RequestBody RecipeChatRequest request) {
        RecipeChatResponse response = chefRagService.askChef(request);
        return ResponseEntity.ok(response);
    }
}
