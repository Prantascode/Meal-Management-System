package com.pranta.MealManagement.Controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    // UPDATED: Using gemini-2.5-flash and removed the ?key= parameter from the URL
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzeMessData(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Safe Extraction
            Map<String, Object> stats = (Map<String, Object>) payload.get("context");
            if (stats == null) {
                return ResponseEntity.badRequest().body(Map.of("analysis", "Error: No data context provided."));
            }

            String meals = stats.getOrDefault("totalMeals", "0").toString();
            String deposits = stats.getOrDefault("totalDeposits", "0").toString();
            String members = stats.getOrDefault("memberCount", "0").toString();

            // 2. Prompt
            String prompt = String.format(
                "You are an expert Mess Manager in Bangladesh. Analyze this data: " +
                "Total Meals: %s, Total Deposits: ৳%s, Total Members: %s. " +
                "1. Calculate the current meal rate. " +
                "2. Check if the deposits are sufficient. " +
                "3. Give 3 tips to lower the meal rate using seasonal Bangladeshi vegetables. " +
                "Format clearly with bold headers.",
                meals, deposits, members
            );

            // 3. Prepare JSON body
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                    "parts", List.of(Map.of("text", prompt))
                ))
            );

            // 4. Set Headers (Key moved to x-goog-api-key)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey); 
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 5. API Call
            Map<String, Object> response;
            try {
                // Call using headers rather than URL parameters
                response = restTemplate.postForObject(GEMINI_URL, entity, Map.class);
            } catch (Exception apiEx) {
                System.err.println("Gemini API Error: " + apiEx.getMessage());
                return ResponseEntity.status(502).body(Map.of("analysis", "AI Connection Failed: " + apiEx.getMessage()));
            }

            // 6. Safe Extraction of the text
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    String analysis = (String) parts.get(0).get("text");

                    return ResponseEntity.ok(Map.of("analysis", analysis));
                }
            }

            return ResponseEntity.ok(Map.of("analysis", "AI returned an empty response. Check your API quota."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("analysis", "System Error: " + e.getMessage()));
        }
    }
}