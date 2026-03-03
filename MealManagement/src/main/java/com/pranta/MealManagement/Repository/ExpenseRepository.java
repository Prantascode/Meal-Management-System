package com.pranta.MealManagement.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Expense;
import com.pranta.MealManagement.Entity.Mess;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByMessAndDateBetweenOrderByDateDesc(Mess mess, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.mess = :mess AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesByMessBetweenDates(
        @Param("mess") Mess mess, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    List<Expense> findByMessAndCategory(Mess mess, Expense.ExpenseCategory category);
}