package com.pranta.MealManagement.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.ExpenseDto;
import com.pranta.MealManagement.Entity.Expense;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Repository.ExpenseRepository;
import com.pranta.MealManagement.Repository.MemberRepository;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private MemberRepository memberRepository;

    public ExpenseDto addExpense(ExpenseDto expenseDto){
        Member addedBy = memberRepository.findById(expenseDto.getAddedById())
                .orElseThrow(() -> new RuntimeException("Member not Found"));

        Expense expense = new Expense();
        expense.setDescription(expenseDto.getDescription());
        expense.setAmount(expenseDto.getAmount());
        expense.setDate(expenseDto.getDate());
        expense.setCategory(expenseDto.getCategory());
        expense.setAddedBy(addedBy);

        Expense saveExpense = expenseRepository.save(expense);
        return convertToDto(saveExpense);
    }

    private ExpenseDto convertToDto(Expense expense){
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setCategory(expense.getCategory());
        dto.setAddedById(expense.getAddedBy().getId());
        dto.setAddedByName(expense.getAddedBy().getName());
        return dto;
    }
}
