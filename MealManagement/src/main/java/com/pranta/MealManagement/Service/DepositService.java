package com.pranta.MealManagement.Service;


import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Entity.Deposit;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Repository.DepositRepository;
import com.pranta.MealManagement.Repository.MemberRepository;

@Service
public class DepositService {
    
    @Autowired
    public DepositRepository depositRepository;

    @Autowired
    public MemberRepository memberRepository;

    public Deposit addDeposit(Long memberId, BigDecimal amount, String discription){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        Deposit deposit = new Deposit(memberId, member, amount, null, discription);
        return depositRepository.save(deposit);
    }
}
