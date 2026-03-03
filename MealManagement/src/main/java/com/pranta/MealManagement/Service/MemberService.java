package com.pranta.MealManagement.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.MemberDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.MessRepository;

@Service
public class MemberService {
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessRepository messRepository;

    public MemberDto registerMember(MemberDto memberDto, Long messId){
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("Member with this email already exists");
        }

        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        Member member = convertToEntity(memberDto);
        member.setMess(mess); 
        
        Member savedMember = memberRepository.save(member);
        return convertToDto(savedMember);
    }

    public List<MemberDto> getActiveMembersByMess(Long messId){
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        return memberRepository.findActiveMembersByMess(mess)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public Optional<MemberDto> getMemberByIdAndMess(Long id, Long messId){
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        return memberRepository.findByIdAndMess(id, mess)
                .map(this::convertToDto);
    }

    public MemberDto updateMember(Long id, MemberDto memberDto, Long messId) {
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        Member member = memberRepository.findByIdAndMess(id, mess)
                .orElseThrow(() -> new RuntimeException("Member not found in your mess"));

        if (!member.getEmail().equals(memberDto.getEmail()) && 
            memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("Email already in use by another member");
        }

        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());
        member.setRole(memberDto.getRole());
        member.setActive(memberDto.isActive());

        Member updatedMember = memberRepository.save(member);
        return convertToDto(updatedMember);
    }

    public void deactivateMember(Long id, Long messId){
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        Member member = memberRepository.findByIdAndMess(id, mess)
            .orElseThrow(() -> new RuntimeException("Member Not Found in your mess"));
            
        member.setActive(false);
        memberRepository.save(member);  
    }

    private MemberDto convertToDto(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.setId(member.getId());
        memberDto.setName(member.getName());
        memberDto.setEmail(member.getEmail());
        memberDto.setPhone(member.getPhone());
        memberDto.setRole(member.getRole());
        memberDto.setActive(member.isActive());
        memberDto.setMessId(member.getMess() != null ? member.getMess().getId() : null); 
        memberDto.setMessName(member.getMess() != null ? member.getMess().getMessName() : null);
        return memberDto;
    }

    private Member convertToEntity(MemberDto dto){
        Member member = new Member();
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setRole(dto.getRole() != null ? dto.getRole() : Member.Role.MEMBER);
        member.setActive(dto.isActive());
        member.setPassword(dto.getPassword());
        return member;
    }
}
