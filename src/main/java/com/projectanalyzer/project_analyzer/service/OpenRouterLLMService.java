package com.projectanalyzer.project_analyzer.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenRouterLLMService implements LLMService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private static final String API_URL =
            "https://openrouter.ai/api/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generateResponse(String prompt) {
        return callOpenRouter(prompt);
    }

    private String callOpenRouter(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            JSONObject body = new JSONObject();

            // 🔥 MODEL (working one you tested)
            body.put("model", "meta-llama/llama-3-8b-instruct");

            body.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));

            HttpEntity<String> request =
                    new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            API_URL,
                            HttpMethod.POST,
                            request,
                            String.class
                    );

            return extractResponse(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("OpenRouter API Error: " + e.getMessage());
        }
    }

    private String extractResponse(String responseBody) {
        JSONObject json = new JSONObject(responseBody);

        return json
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}