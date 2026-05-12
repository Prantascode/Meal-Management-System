package com.pranta.MealManagement.Controller;

import com.pranta.MealManagement.Config.GoogleOAuthConfig;
import com.pranta.MealManagement.Entity.AdminGoogleToken;
import com.pranta.MealManagement.Entity.GoogleOAuthState;
import com.pranta.MealManagement.Repository.AdminGoogleTokenRepository;
import com.pranta.MealManagement.Repository.GoogleOAuthStateRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/google/oauth")
@RequiredArgsConstructor
public class GoogleOAuthCallbackController {

    private final GoogleOAuthConfig googleOAuthConfig;
    private final AdminGoogleTokenRepository tokenRepository;
    private final GoogleOAuthStateRepository googleOAuthStateRepository;
    private final RestTemplate restTemplate;

    @GetMapping("/callback")
    @Transactional
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        GoogleOAuthState savedState = googleOAuthStateRepository.findByState(state)
                .orElseThrow(() -> new RuntimeException("Invalid OAuth state"));

        String adminEmail = savedState.getAdminEmail();

        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(googleOAuthConfig.getClientId(), StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(googleOAuthConfig.getClientSecret(), StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(googleOAuthConfig.getRedirectUri(), StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        Map<String, Object> tokenData = response.getBody();

        if (tokenData == null || tokenData.get("access_token") == null) {
            throw new RuntimeException("Failed to get Google access token");
        }

        String accessToken = (String) tokenData.get("access_token");
        String refreshToken = (String) tokenData.get("refresh_token");
        Integer expiresIn = (Integer) tokenData.get("expires_in");

        AdminGoogleToken token = tokenRepository.findByAdminEmail(adminEmail)
                .orElse(new AdminGoogleToken());

        token.setAdminEmail(adminEmail);
        token.setAccessToken(accessToken);

        if (refreshToken != null) {
            token.setRefreshToken(refreshToken);
        }

        token.setExpiresIn(expiresIn != null ? Long.valueOf(expiresIn) : null);
        token.setCreatedAt(System.currentTimeMillis());

        tokenRepository.save(token);

        googleOAuthStateRepository.deleteByState(state);

        return ResponseEntity.ok("Gmail connected successfully for: " + adminEmail);
    }
}