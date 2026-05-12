package com.nihongodev.platform.infrastructure.llm;

import com.nihongodev.platform.application.port.out.LlmPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ClaudeLlmAdapter implements LlmPort {

    private static final Logger log = LoggerFactory.getLogger(ClaudeLlmAdapter.class);

    private final RestClient restClient;
    private final String model;

    public ClaudeLlmAdapter(@Value("${app.llm.api-key:}") String apiKey,
                            @Value("${app.llm.model:claude-sonnet-4-6-20250514}") String model,
                            @Value("${app.llm.base-url:https://api.anthropic.com}") String baseUrl) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public String generate(String systemPrompt, String userPrompt) {
        Map<String, Object> request = Map.of(
                "model", model,
                "max_tokens", 2048,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", userPrompt))
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/v1/messages")
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("content")) {
                List<?> content = (List<?>) response.get("content");
                if (!content.isEmpty()) {
                    Map<?, ?> firstBlock = (Map<?, ?>) content.get(0);
                    return (String) firstBlock.get("text");
                }
            }
            log.warn("Empty response from LLM API");
            return "Generation failed — please try again.";
        } catch (Exception e) {
            log.error("LLM API call failed: {}", e.getMessage());
            throw new RuntimeException("CV generation service unavailable", e);
        }
    }
}
