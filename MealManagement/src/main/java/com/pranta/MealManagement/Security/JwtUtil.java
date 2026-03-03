package com.pranta.MealManagement.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    private final String SECRET_STRING = "ThisIsAReallyLongSuperSecretKeyForHS256JWT1234567890";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    
    private final long accessTokenExpiration = 1000L * 60 * 60; // 1 hour

    public String generateAccessToken(String email, String role, Long messId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) 
                .claim("messId", messId) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Long extractMessId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object messId = claims.get("messId");
            if (messId instanceof Number) {
                return ((Number) messId).longValue();
            }
            return null;
        } catch (Exception e) {
            log.error("Error extracting messId: {}", e.getMessage());
            return null;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}