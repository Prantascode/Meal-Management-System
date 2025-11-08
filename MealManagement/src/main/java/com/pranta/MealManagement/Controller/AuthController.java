package com.pranta.MealManagement.Controller;


import com.pranta.MealManagement.Dtos.LoginRequestDto;
import com.pranta.MealManagement.Dtos.RefreshTokenRequest;
import com.pranta.MealManagement.Dtos.RegisterDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.RefreshToken;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Security.JwtUtil;
import com.pranta.MealManagement.Service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        if (memberRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Member member = new Member();
        member.setName(registerDto.getName());
        member.setEmail(registerDto.getEmail());
        member.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        member.setRole(registerDto.getRole());

        memberRepository.save(member);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        Member user = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return ResponseEntity.ok(new AuthResponse(
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken.getToken()
        ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getMember)
                .map(member -> {
                    String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getRole().name());
                    return ResponseEntity.ok(new AuthResponse(
                            member.getEmail(),
                            member.getRole().name(),
                            accessToken,
                            requestRefreshToken
                    ));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokenService.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenService::deleteRefreshToken);
        return ResponseEntity.ok("Logout successful");
    }

    record AuthResponse(String email, String role, String accessToken, String refreshToken) {}
}