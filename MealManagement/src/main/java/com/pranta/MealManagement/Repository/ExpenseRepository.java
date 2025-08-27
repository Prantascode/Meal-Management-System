package com.pranta.MealManagement.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesBetweenDates(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    List<Expense> findByCategory(Expense.ExpenseCategory category);
}
