package com.example.demo.rag.seeder;

import com.example.demo.rag.client.GeminiApiClient;
import com.example.demo.rag.client.exception.GeminiRateLimitException;
import com.example.demo.rag.model.Recipe;
import com.example.demo.rag.repository.RecipeRepository;
import com.example.demo.rag.util.VectorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Component
public class CsvDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvDataSeeder.class);

    private final RecipeRepository recipeRepository;
    private final GeminiApiClient geminiApiClient;
    private final String csvPath;
    private final int batchLimit;
    private final long rateDelayMs;
    private final String apiKey;

    public CsvDataSeeder(
            RecipeRepository recipeRepository,
            GeminiApiClient geminiApiClient,
            @Value("${rag.ingestion.csv-path:recipes.csv}") String csvPath,
            @Value("${rag.ingestion.batch-limit:100}") int batchLimit,
            @Value("${rag.ingestion.rate-delay-ms:2000}") long rateDelayMs,
            @Value("${gemini.api.key:}") String apiKey
    ) {
        this.recipeRepository = recipeRepository;
        this.geminiApiClient = geminiApiClient;
        this.csvPath = csvPath;
        this.batchLimit = batchLimit;
        this.rateDelayMs = rateDelayMs;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    @Override
    public void run(String... args) {
        if (recipeRepository.count() > 0) {
            log.info("Recipe table already contains data. Skipping CSV ingestion.");
            return;
        }

        if (apiKey.isEmpty()) {
            log.warn("Gemini API key is not configured. Skipping CSV data ingestion.");
            return;
        }

        Path path = Path.of(csvPath);
        if (!Files.exists(path)) {
            log.warn("CSV file not found at path: {}. Skipping ingestion.", path.toAbsolutePath());
            return;
        }

        log.info("Starting CSV data ingestion from {} with batch limit of {}...", path.toAbsolutePath(), batchLimit);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setAllowMissingColumnNames(true)
                .setTrim(true)
                .build();

        int processed = 0;
        Set<String> seenRecipeNames = new HashSet<>();

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = csvFormat.parse(reader)) {

            for (CSVRecord record : parser) {
                if (processed >= batchLimit) {
                    log.info("Reached batch limit of {}. Ingestion stopped.", batchLimit);
                    break;
                }

                String recipeName = getRecordValue(record, "recipe_name");
                if (recipeName == null || recipeName.isBlank()) {
                    continue;
                }

                String normalizedName = recipeName.trim().toLowerCase();
                if (!seenRecipeNames.add(normalizedName) || recipeRepository.existsByRecipeName(recipeName.trim())) {
                    log.debug("Skipping duplicate recipe: {}", recipeName);
                    continue;
                }

                String cuisinePath = getRecordValue(record, "cuisine_path");
                String ingredients = getRecordValue(record, "ingredients");
                String directions = getRecordValue(record, "directions");
                String imgSrc = getRecordValue(record, "img_src");
                Integer servings = parseInteger(getRecordValue(record, "servings"));
                Double rating = parseDouble(getRecordValue(record, "rating"));

                if (ingredients == null) ingredients = "";
                if (directions == null) directions = "";
                if (cuisinePath == null) cuisinePath = "";

                String searchContent = "Recipe: " + recipeName +
                        "\nCategory: " + cuisinePath +
                        "\nIngredients: " + ingredients +
                        "\nDirections: " + directions;

                try {
                    float[] embedding = geminiApiClient.generateEmbedding(searchContent);
                    String vectorString = VectorUtils.toVectorString(embedding);

                    Recipe recipe = Recipe.builder()
                            .recipeName(recipeName.trim())
                            .cuisinePath(cuisinePath)
                            .servings(servings)
                            .rating(rating)
                            .imgSrc(imgSrc)
                            .ingredients(ingredients)
                            .directions(directions)
                            .searchContent(searchContent)
                            .embedding(vectorString)
                            .build();

                    recipeRepository.save(recipe);
                    processed++;
                    log.info("Ingested recipe {}/{}: {}", processed, batchLimit, recipeName);

                    if (rateDelayMs > 0 && processed < batchLimit) {
                        try {
                            Thread.sleep(rateDelayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            log.warn("Ingestion interrupted during rate delay.");
                            break;
                        }
                    }
                } catch (GeminiRateLimitException e) {
                    log.error("Rate limit hit during ingestion of recipe '{}': {}", recipeName, e.getMessage());
                    try {
                        Thread.sleep(Math.max(rateDelayMs * 2, 5000));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } catch (Exception e) {
                    log.error("Failed to process recipe '{}': {}", recipeName, e.getMessage(), e);
                }
            }

            log.info("Finished CSV data ingestion. Successfully ingested {} recipes.", processed);
        } catch (Exception e) {
            log.error("Failed to read CSV file: {}", e.getMessage(), e);
        }
    }

    private String getRecordValue(CSVRecord record, String column) {
        try {
            return record.isMapped(column) ? record.get(column) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(String val) {
        if (val == null || val.isBlank()) return null;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        if (val == null || val.isBlank()) return null;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
