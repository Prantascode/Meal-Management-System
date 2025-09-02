package com.pranta.MealManagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.ExpenseDto;
import com.pranta.MealManagement.Service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin("*")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@Valid @RequestBody ExpenseDto expenseDto){
        try{
            ExpenseDto createdExpense = expenseService.addExpense(expenseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }
}
