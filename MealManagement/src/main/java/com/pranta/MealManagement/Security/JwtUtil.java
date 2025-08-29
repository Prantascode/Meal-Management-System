package com.pranta.MealManagement.Security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "ThisIsAReallyLongSuperSecretKeyForHS256JWT1234567890";
    private final long expiration = 86400000; // 1 day

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }
    public boolean validateToken(String token, UserDetails userDetails) {
    final String email = extractEmail(token);
    return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
}

private boolean isTokenExpired(String token) {
    return Jwts.parser().setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration()
            .before(new Date());
}

}
