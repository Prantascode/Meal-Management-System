package com.pranta.MealManagement.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.MemberDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Repository.MemberRepository;

@Service
public class MemberService {
    
    @Autowired
    private MemberRepository memberRepository;

    public MemberDto registerMember(MemberDto memberDto){
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("Member with this email already exists");
        }

        Member member = convertToEntity(memberDto);
        Member savedMember = memberRepository.save(member);
        return convertToDto(savedMember);
    }

    public List<MemberDto> getAllActiveMembers(){
        return memberRepository.findActiveMembers()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private MemberDto convertToDto(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.setId(member.getId());
        memberDto.setName(member.getName());
        memberDto.setEmail(member.getEmail());
        memberDto.setPhone(member.getPhone());
        memberDto.setRole(member.getRole());
        memberDto.setActive(member.isActive());
        return memberDto;
    }
    private Member convertToEntity(MemberDto dto){
        Member member = new Member();
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setRole(dto.getRole() != null ? dto.getRole() : Member.Role.MEMBER);
        member.setActive(dto.isActive());

        return member;
    }
}
