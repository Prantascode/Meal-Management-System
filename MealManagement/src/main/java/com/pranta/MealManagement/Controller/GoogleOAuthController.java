package com.pranta.MealManagement.Controller;

import com.pranta.MealManagement.Config.GoogleOAuthConfig;
import com.pranta.MealManagement.Entity.GoogleOAuthState;
import com.pranta.MealManagement.Repository.GoogleOAuthStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/google/oauth")
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final GoogleOAuthConfig googleOAuthConfig;
    private final GoogleOAuthStateRepository googleOAuthStateRepository;

    @GetMapping("/url")
    public ResponseEntity<String> getGoogleOAuthUrl(Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        String adminEmail = principal.getName();

        String state = UUID.randomUUID().toString();

        GoogleOAuthState oauthState = GoogleOAuthState.builder()
                .state(state)
                .adminEmail(adminEmail)
                .createdAt(System.currentTimeMillis())
                .build();

        googleOAuthStateRepository.save(oauthState);

        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + URLEncoder.encode(googleOAuthConfig.getClientId(), StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(googleOAuthConfig.getRedirectUri(), StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=" + URLEncoder.encode(googleOAuthConfig.getScope(), StandardCharsets.UTF_8)
                + "&access_type=offline"
                + "&prompt=consent"
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

        return ResponseEntity.ok(url);
    }
}