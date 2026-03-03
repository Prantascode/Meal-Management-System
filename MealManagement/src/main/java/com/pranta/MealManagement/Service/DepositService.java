package com.pranta.MealManagement.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.DepositDto;
import com.pranta.MealManagement.Entity.Deposit;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;
import com.pranta.MealManagement.Repository.DepositRepository;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.MessRepository;

import jakarta.transaction.Transactional;

@Service
public class DepositService {
    
    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessRepository messRepository;

    public DepositDto addDeposit(Long memberId, Long messId, BigDecimal amount, String description){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        Mess mess = messRepository.findById(messId)
            .orElseThrow(() -> new RuntimeException("Mess not found"));

        Deposit deposit = new Deposit(member, amount, description, mess);
        Deposit savedDeposit = depositRepository.save(deposit);

        return convertToDto(savedDeposit);
    }

    public List<DepositDto> getDepositByMemberAndMess(Long memberId, Long messId){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        
        Mess mess = messRepository.findById(messId)
            .orElseThrow(() -> new RuntimeException("Mess not found"));
        
        List<Deposit> deposits = depositRepository.findByMemberAndMessOrderByDepositDateDesc(member, mess);
        
        return deposits.stream()
                        .map(this::convertToDto)
                        .toList();
    }

    public BigDecimal getDepositByMemberAndMessAndDateRange(Long memberId, Long messId, LocalDateTime startDate, LocalDateTime endDate){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        
        Mess mess = messRepository.findById(messId)
            .orElseThrow(() -> new RuntimeException("Mess not found"));

        BigDecimal total = depositRepository.getTotalDepositsByMemberAndMessBetweenDates(member, mess, startDate, endDate);

        return total != null ? total : BigDecimal.ZERO;
    }

    public List<DepositDto> getDepositsByMessAndDateRange(Long messId, LocalDateTime startDate, LocalDateTime endDate){
        Mess mess = messRepository.findById(messId)
            .orElseThrow(() -> new RuntimeException("Mess not found"));

        List<Deposit> deposits = depositRepository.findByMessAndDepositDateBetweenOrderByDepositDateDesc(mess, startDate, endDate);
        return deposits.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public DepositDto updateDeposit(Long id, DepositDto dto) {
        Deposit deposit = depositRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Deposit record not found"));

        deposit.setAmount(dto.getAmount());
        deposit.setDescription(dto.getDescription());
        
        if (dto.getDate() != null) {
            deposit.setDepositDate(dto.getDate());
        }

        Deposit updated = depositRepository.save(deposit);
        return convertToDto(updated);
    }

    public void deleteDeposit(Long id) {
        if (!depositRepository.existsById(id)) {
            throw new RuntimeException("Deposit record not found");
        }
        depositRepository.deleteById(id);
    }

    private DepositDto convertToDto(Deposit deposit){
        DepositDto dto = new DepositDto();
        dto.setId(deposit.getId());
        dto.setMemberId(deposit.getMember().getId());
        dto.setMemberName(deposit.getMember().getName());
        dto.setMessId(deposit.getMess().getId()); 
        dto.setAmount(deposit.getAmount());
        dto.setDate(deposit.getDepositDate());
        dto.setDescription(deposit.getDescription());

        return dto;
    }
}