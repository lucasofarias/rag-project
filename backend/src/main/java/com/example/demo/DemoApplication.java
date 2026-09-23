package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DemoApplication {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        Environment env = context.getEnvironment();

        String geminiApiKey = env.getProperty("gemini.api.key", "");
        String ragBatchLimit = env.getProperty("rag.ingestion.batch-limit", "");

        String maskedKey = (geminiApiKey != null && geminiApiKey.length() > 8)
                ? geminiApiKey.substring(0, 4) + "..." + geminiApiKey.substring(geminiApiKey.length() - 4)
                : ((geminiApiKey == null || geminiApiKey.isBlank()) ? "<NOT_SET>" : "***");

        log.info("==================================================");
        log.info(">>> ENVIRONMENT CHECK (Loaded via .env / Env Vars):");
        log.info(">>> GEMINI_API_KEY: {} (length: {})", maskedKey, (geminiApiKey != null ? geminiApiKey.length() : 0));
        log.info(">>> RAG_BATCH_LIMIT: {}", ragBatchLimit);
        log.info("==================================================");
    }
}
