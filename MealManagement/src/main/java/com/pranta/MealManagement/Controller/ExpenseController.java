package com.pranta.MealManagement.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.security.Principal; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pranta.MealManagement.Dtos.ExpenseDto;
import com.pranta.MealManagement.Entity.Expense;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private MemberRepository memberRepository;

    private Long getMessIdFromPrincipal(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        return memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getMess().getId();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@Valid @RequestBody ExpenseDto expenseDto, Principal principal) {
        try {
            expenseDto.setMessId(getMessIdFromPrincipal(principal));
            ExpenseDto createdExpense = expenseService.addExpense(expenseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpensesByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Principal principal
    ) {
        Long messId = getMessIdFromPrincipal(principal);
        List<ExpenseDto> expense = expenseService.getExpensesByMessAndDateRange(messId, startDate, endDate);
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalExpensesByDateRange(
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
         Principal principal
    ) {
        Long messId = getMessIdFromPrincipal(principal);
        BigDecimal totalExpense = expenseService.getTotalExpensesByMessAndDateRange(messId, startDate, endDate);
        return ResponseEntity.ok(totalExpense);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseDto>> getExpenseByCategory(@PathVariable Expense.ExpenseCategory category, Principal principal) {
        Long messId = getMessIdFromPrincipal(principal);
        List<ExpenseDto> expense = expenseService.getExpenseByMessAndCategory(messId, category);
        return ResponseEntity.ok(expense);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseDto expenseDto) {
        try {
            ExpenseDto updateDto = expenseService.updateExpense(id, expenseDto);
            return ResponseEntity.ok(updateDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}