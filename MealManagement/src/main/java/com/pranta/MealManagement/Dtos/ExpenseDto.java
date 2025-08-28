package com.pranta.MealManagement.Dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pranta.MealManagement.Entity.Expense;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
     private Long id;

    @NotBlank(message = "Discription is required")
    private String description;

    @NotNull(message = "Amount is Required")
    @DecimalMin(value = "0.0",inclusive = false,message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Category is requried")
    private Expense.ExpenseCategory category;

   @NotNull(message = "Added by member is required")
    private Long addedById;
    private String addedByName;

}
