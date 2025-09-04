package com.pranta.MealManagement.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.ExpenseDto;
import com.pranta.MealManagement.Entity.Expense;
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

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpensesByDateRangeet(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate
    ){
        List<ExpenseDto> expense = expenseService.getExpensesByDateRange(startDate, endDate);
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalExpensesByDateRange(
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate
    ){
        BigDecimal totalExpense = expenseService.getTotalExpensesByDateRange(startDate, endDate);
        return ResponseEntity.ok(totalExpense);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseDto>> getExpenseByCategory(@PathVariable Expense.ExpenseCategory category){
        List<ExpenseDto> expense = expenseService.getExpenseByCategory(category);
        return ResponseEntity.ok(expense);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable Long id,@Valid @RequestBody ExpenseDto expenseDto){
        try{
            ExpenseDto updateDto = expenseService.updateExpense(id, expenseDto);
                return ResponseEntity.ok(updateDto);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        try{
            expenseService.deleteExpense(id);
            return ResponseEntity.ok().build();
        }
        catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
