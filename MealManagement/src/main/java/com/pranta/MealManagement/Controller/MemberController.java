package com.pranta.MealManagement.Controller;


import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.MemberDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Service.MemberService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long getMessIdFromPrincipal(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        Member user = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getMess().getId();
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MemberDto> registerMember(@Valid @RequestBody MemberDto memberDto, Principal principal) {
        try {
            Long messId = getMessIdFromPrincipal(principal);
            memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
            MemberDto newMember = memberService.registerMember(memberDto, messId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllActiveMembers(Principal principal) {
        Long messId = getMessIdFromPrincipal(principal);
        List<MemberDto> members = memberService.getActiveMembersByMess(messId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id, Principal principal) {
        Long messId = getMessIdFromPrincipal(principal);
        return memberService.getMemberByIdAndMess(id, messId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable Long id, @Valid @RequestBody MemberDto memberDto, Principal principal) {
        try {
            Long messId = getMessIdFromPrincipal(principal);
            MemberDto updatedMember = memberService.updateMember(id, memberDto, messId);
            return ResponseEntity.ok(updatedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id, Principal principal) {
        try {
            Long messId = getMessIdFromPrincipal(principal);
            memberService.deactivateMember(id, messId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}