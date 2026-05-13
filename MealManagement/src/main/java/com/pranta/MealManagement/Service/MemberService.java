package com.pranta.MealManagement.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.MemberDto;
import com.pranta.MealManagement.Dtos.MyProfileDto;
import com.pranta.MealManagement.Dtos.UpdateMyProfileDto;
import com.pranta.MealManagement.Dtos.UpdatePasswordDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;
import com.pranta.MealManagement.Entity.Member.Role;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.MessRepository;

import jakarta.transaction.Transactional;

@Service
public class MemberService {
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessRepository messRepository;

    @Autowired
    private GmailSenderService gmailSenderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDto registerMember(MemberDto memberDto, Long messId, String adminEmail) {

        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("Member with this email already exists");
        }

        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        String temporaryPassword = generateTemporaryPassword();

        Member member = convertToEntity(memberDto);
        member.setMess(mess);

        // If Member has password field
        member.setPassword(passwordEncoder.encode(temporaryPassword));

        // If Member has role/status fields
        member.setRole(Role.MEMBER);
        member.setActive(true);

        Member savedMember = memberRepository.save(member);

        try {
            gmailSenderService.sendMemberPasswordEmail(
                    adminEmail,
                    savedMember.getEmail(),
                    temporaryPassword
            );
        } catch (Exception e) {
            throw new RuntimeException("Member created, but email sending failed: " + e.getMessage());
        }

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
    public MyProfileDto getCurrentUser(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new MyProfileDto(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getRole(),
                member.isActive(),
                member.getMess() != null ? member.getMess().getMessName() : null,
                member.getMess() != null ? member.getMess().getId() : null
        );
    }

    @Transactional
    public MyProfileDto updateMyProfile(String email, UpdateMyProfileDto dto) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        member.setName(dto.getName());
        member.setPhone(dto.getPhone());

        Member updatedMember = memberRepository.save(member);

        return new MyProfileDto(
                updatedMember.getId(),
                updatedMember.getName(),
                updatedMember.getEmail(),
                updatedMember.getPhone(),
                updatedMember.getRole(),
                updatedMember.isActive(),
                updatedMember.getMess() != null ? updatedMember.getMess().getMessName() : null,
                updatedMember.getMess() != null ? updatedMember.getMess().getId() : null
        );
    }

    @Transactional
    public void updateMyPassword(String email, UpdatePasswordDto dto) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        member.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        memberRepository.save(member);
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

    private Member convertToEntity(MemberDto dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setRole(dto.getRole() != null ? dto.getRole() : Member.Role.MEMBER);
        member.setActive(dto.isActive());
        return member;
    }

    private String generateTemporaryPassword() {
    return UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 6);
    }
}
