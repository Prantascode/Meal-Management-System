package com.pranta.MealManagement.Controller;


import com.pranta.MealManagement.Dtos.LoginRequestDto;
import com.pranta.MealManagement.Dtos.RegisterDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ✅ Admin-secured endpoint to register user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        if (memberRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Member member = new Member();
        member.setName(registerDto.getName());
        member.setEmail(registerDto.getEmail());
        member.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        member.setRole(registerDto.getRole()); // ADMIN will set role when creating

        memberRepository.save(member);
        return ResponseEntity.ok("User registered successfully");
    }

    // ✅ Login with DTO
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        Member existingUser = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(existingUser.getEmail(), existingUser.getRole().name());

        return ResponseEntity.ok(new AuthResponse(
                existingUser.getEmail(),
                existingUser.getRole().name(),
                token
        ));
    }

    // ✅ Response DTO for login
    record AuthResponse(String email, String role, String token) {}
}