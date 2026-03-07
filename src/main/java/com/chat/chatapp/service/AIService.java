package com.chat.chatapp.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public CompletableFuture<String> getAIResponse(String userMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Initialize the SDK Client with the API key
                Client client = Client.builder()
                        .apiKey(apiKey)
                        .build();

                // Call Gemini using the SDK
                // Note: Using gemini-2.5-flash as requested.
                GenerateContentResponse response = client.models.generateContent(
                        "gemini-2.5-flash",
                        userMessage,
                        null);

                if (response != null && response.text() != null) {
                    return response.text();
                }
                return "I couldn't generate a response at this time.";
            } catch (Exception e) {
                return "Error connecting to AI service: " + e.getMessage();
            }
        });
    }
}
