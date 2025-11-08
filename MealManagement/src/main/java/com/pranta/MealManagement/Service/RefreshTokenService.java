package com.pranta.MealManagement.Service;


import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.RefreshToken;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.RefreshTokenRepository;
import com.pranta.MealManagement.Security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

     private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public RefreshToken createRefreshToken(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete existing refresh token
        refreshTokenRepository.findByMember(member).ifPresent(this::deleteRefreshToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setMember(member);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            deleteRefreshToken(token);
            throw new RuntimeException("Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(this::deleteRefreshToken);
    }

    @Transactional
    public void deleteByMember(Member member) {
        refreshTokenRepository.findByMember(member).ifPresent(this::deleteRefreshToken);
    }
}