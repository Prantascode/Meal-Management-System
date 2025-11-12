package com.pranta.MealManagement.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.DepositDto;
import com.pranta.MealManagement.Entity.Deposit;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Repository.DepositRepository;
import com.pranta.MealManagement.Repository.MemberRepository;

@Service
public class DepositService {
    
    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private MemberRepository memberRepository;

    public DepositDto addDeposit(Long memberId, BigDecimal amount, String description){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        Deposit deposit = new Deposit(member, amount, description);
        Deposit savedDeposit =  depositRepository.save(deposit);

        return convertToDto(savedDeposit);
    }

    public List<DepositDto> getDepositByMember(Long memberId){
       Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        
        List<Deposit> deposits = depositRepository.findByMemberOrderByDepositDateDesc(member);
        
        return deposits.stream()
                        .map(this::convertToDto)
                        .toList();
    }

    public BigDecimal getDepositByMemberAndDateRange(Long memberId,LocalDateTime startDate,LocalDateTime endDate){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        BigDecimal total = depositRepository.getTotalDepositsByMemberBetweenDates(member, startDate, endDate);

        return total != null ? total : BigDecimal.ZERO;
    }

    private DepositDto convertToDto(Deposit deposit){
        DepositDto dto = new DepositDto();
        dto.setMemberId(deposit.getMember().getId());
        dto.setAmount(deposit.getAmount());
        dto.setDate(deposit.getDepositDate());
        dto.setDescription(deposit.getDescription());

        return dto;
    }
}
