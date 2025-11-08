package com.pranta.MealManagement.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "ThisIsAReallyLongSuperSecretKeyForHS256JWT1234567890";
    private final long accessTokenExpiration = 1000L * 60 * 60; // 1 hour
    private final long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 7; // 7 days

    // Generate Access Token
    public String generateAccessToken(String email, String role) {
        return generateToken(email, role, accessTokenExpiration);
    }

    // Generate Refresh Token
    public String generateRefreshToken(String email) {
        return generateToken(email, "REFRESH", refreshTokenExpiration);
    }

    private String generateToken(String email, String role, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Extract email from token
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        return (String) Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Check expiration
    public boolean isTokenExpired(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Extract expiration date
    public Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}