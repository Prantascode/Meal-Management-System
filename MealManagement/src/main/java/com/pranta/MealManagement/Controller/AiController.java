package com.pranta.MealManagement.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzeMessData(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> context = (Map<String, Object>) payload.get("context");

            if (context == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("analysis", "Error: No data context provided."));
            }

            BigDecimal totalMeals = getBigDecimalValue(context, "totalMeals");
            BigDecimal totalDeposits = getBigDecimalValue(context, "totalDeposits");
            BigDecimal totalExpenses = getBigDecimalValue(context, "totalExpenses");
            BigDecimal memberCount = getBigDecimalValue(context, "memberCount");

            BigDecimal mealRate = BigDecimal.ZERO;

            if (totalMeals.compareTo(BigDecimal.ZERO) > 0) {
                mealRate = totalExpenses.divide(totalMeals, 2, RoundingMode.HALF_UP);
            }

            BigDecimal remainingBalance = totalDeposits.subtract(totalExpenses);

            String financialStatus;

            if (remainingBalance.compareTo(BigDecimal.ZERO) > 0) {
                financialStatus = "Surplus";
            } else if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) {
                financialStatus = "Deficit";
            } else {
                financialStatus = "Balanced";
            }

            String prompt = String.format(
                    """
                    You are an expert Mess Manager and financial advisor for a Bangladeshi student mess.

                    Analyze the following monthly mess data carefully:

                    Total Meals: %s
                    Total Deposits: ৳%s
                    Total Expenses: ৳%s
                    Total Members: %s
                    Current Meal Rate: ৳%s per meal
                    Remaining Balance: ৳%s
                    Financial Status: %s

                    Give a clear, practical, and well-structured analysis.

                    Your response must include:

                    1. **Monthly Summary**
                       - Explain the overall mess situation in simple words.
                       - Mention total meals, total deposits, total expenses, and member count.

                    2. **Meal Rate Analysis**
                       - Explain whether the meal rate is low, normal, or high for a Bangladeshi student mess.
                       - Mention the calculated meal rate clearly.

                    3. **Deposit vs Expense Analysis**
                       - Explain whether deposits are enough.
                       - If there is a deficit, explain how much more money is needed.
                       - If there is a surplus, explain how much balance remains.

                    4. **Managerial Decision**
                       - Tell the mess manager what action should be taken now.
                       - Example: collect more deposit, reduce bazaar cost, monitor meals, or keep current plan.

                    5. **Cost Reduction Tips**
                       - Give 3 realistic tips to reduce meal cost.
                       - Use common seasonal Bangladeshi vegetables and affordable protein options.
                       - Keep the tips practical for students.

                    6. **Warning or Positive Note**
                       - If expenses are higher than deposits, give a warning.
                       - If deposits are enough, give a positive note.

                    Format the answer with clear bold headings.
                    Do not use unnecessary long paragraphs.
                    Make the answer helpful for a real mess management dashboard.
                    """,
                    totalMeals,
                    totalDeposits,
                    totalExpenses,
                    memberCount,
                    mealRate,
                    remainingBalance,
                    financialStatus
            );

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response;

            try {
                response = restTemplate.postForObject(GEMINI_URL, entity, Map.class);
            } catch (Exception apiEx) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Map.of(
                                "analysis",
                                "AI Connection Failed: " + apiEx.getMessage()
                        ));
            }

            String analysis = extractGeminiText(response);

            if (analysis == null || analysis.isBlank()) {
                return ResponseEntity.ok(
                        Map.of("analysis", "AI returned an empty response. Please check your API quota or request format.")
                );
            }

            return ResponseEntity.ok(Map.of("analysis", analysis));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "analysis",
                            "System Error: " + e.getMessage()
                    ));
        }
    }

    private BigDecimal getBigDecimalValue(Map<String, Object> context, String key) {
        Object value = context.get(key);

        if (value == null) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @SuppressWarnings("unchecked")
    private String extractGeminiText(Map<String, Object> response) {
        if (response == null || !response.containsKey("candidates")) {
            return null;
        }

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        Map<String, Object> firstCandidate = candidates.get(0);

        Map<String, Object> content =
                (Map<String, Object>) firstCandidate.get("content");

        if (content == null || !content.containsKey("parts")) {
            return null;
        }

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        if (parts == null || parts.isEmpty()) {
            return null;
        }

        Object text = parts.get(0).get("text");

        return text != null ? text.toString() : null;
    }
}