package com.example.demo.rag.service;

import com.example.demo.rag.client.GeminiApiClient;
import com.example.demo.rag.dto.RecipeChatRequest;
import com.example.demo.rag.dto.RecipeChatResponse;
import com.example.demo.rag.dto.RecipeMetadata;
import com.example.demo.rag.repository.RecipeRepository;
import com.example.demo.rag.util.VectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChefRagService {

    private static final Logger log = LoggerFactory.getLogger(ChefRagService.class);

    private final RecipeRepository recipeRepository;
    private final GeminiApiClient geminiApiClient;

    public ChefRagService(RecipeRepository recipeRepository, GeminiApiClient geminiApiClient) {
        this.recipeRepository = recipeRepository;
        this.geminiApiClient = geminiApiClient;
    }

    public RecipeChatResponse askChef(RecipeChatRequest request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }

        String question = request.question().trim();
        String cuisineFilter = (request.cuisineFilter() != null && !request.cuisineFilter().isBlank())
                ? request.cuisineFilter().trim()
                : null;
        int topK = request.resolvedTopK();

        log.info("Processing RAG question: '{}', cuisineFilter: '{}', topK: {}", question, cuisineFilter, topK);

        // 1. Generate query embedding
        float[] queryVector = geminiApiClient.generateEmbedding(question);
        String vectorString = VectorUtils.toVectorString(queryVector);

        // 2. Query similar recipes
        List<Object[]> queryResults = recipeRepository.findSimilarRecipes(vectorString, cuisineFilter, topK);

        List<RecipeMetadata> matchedRecipes = new ArrayList<>();
        StringBuilder contextBuilder = new StringBuilder();
        Set<String> seenRecipes = new HashSet<>();

        for (Object[] row : queryResults) {
            String recipeName = row[1] != null ? (String) row[1] : "";
            String normalizedName = recipeName.trim().toLowerCase();

            if (!seenRecipes.add(normalizedName)) {
                continue;
            }

            Long id = row[0] != null ? ((Number) row[0]).longValue() : null;
            String cuisinePath = row[2] != null ? (String) row[2] : "";
            Integer servings = row[3] != null ? ((Number) row[3]).intValue() : null;
            Double rating = row[4] != null ? ((Number) row[4]).doubleValue() : null;
            String imgSrc = row[5] != null ? (String) row[5] : "";
            String ingredients = row[6] != null ? (String) row[6] : "";
            String directions = row[7] != null ? (String) row[7] : "";
            Double similarity = row[8] != null ? ((Number) row[8]).doubleValue() : 0.0;

            matchedRecipes.add(new RecipeMetadata(id, recipeName, cuisinePath, servings, rating, imgSrc, similarity));

            contextBuilder.append("Receita: ").append(recipeName).append("\n")
                    .append("Categoria: ").append(cuisinePath).append("\n")
                    .append("Porções: ").append(servings != null ? servings : "N/D")
                    .append(" | Avaliação: ").append(rating != null ? rating : "N/D").append("\n")
                    .append("Ingredientes: ").append(ingredients).append("\n")
                    .append("Modo de Preparo: ").append(directions).append("\n\n---\n\n");
        }

        String retrievedContext = contextBuilder.length() > 0
                ? contextBuilder.toString().trim()
                : "Nenhuma receita encontrada correspondente à pesquisa.";

        // 3. Construct prompt
        String prompt = """
                És um assistente culinário inteligente e experiente.
                Responde à dúvida do utilizador com base EXCLUSIVAMENTE nas receitas apresentadas no contexto.
                Se a pergunta pedir opções de pratos ou substituições, foca-te nas opções fornecidas.
                Identifica sempre o nome exato da receita e os passos de preparação relevantes.

                Contexto:
                %s

                Pergunta do Utilizador:
                %s
                """.formatted(retrievedContext, question);

        // 4. Generate LLM response
        String answer = geminiApiClient.generateResponse(prompt);

        return new RecipeChatResponse(answer, matchedRecipes);
    }
}
