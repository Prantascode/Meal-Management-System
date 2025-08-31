package com.pranta.MealManagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.MemberDto;
import com.pranta.MealManagement.Service.MemberService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {
    
    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDto> registerMember(@Valid @RequestBody MemberDto memberDto){
        try{
            MemberDto registerMember = memberService.registerMember(memberDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registerMember);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllActiveMembers(){
        List<MemberDto> members = memberService.getAllActiveMembers();
        return ResponseEntity.ok(members);
    }
}
