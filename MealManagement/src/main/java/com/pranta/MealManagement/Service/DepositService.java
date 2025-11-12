package com.pranta.MealManagement.Service;


import java.math.BigDecimal;

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

    private DepositDto convertToDto(Deposit deposit){
        DepositDto dto = new DepositDto();
        dto.setMemberId(deposit.getMember().getId());
        dto.setAmount(deposit.getAmount());
        dto.setDate(deposit.getDepositDate());
        dto.setDescription(deposit.getDescription());

        return dto;
    }
}
