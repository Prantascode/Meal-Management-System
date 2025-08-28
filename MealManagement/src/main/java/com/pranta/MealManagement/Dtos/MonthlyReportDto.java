package com.pranta.MealManagement.Dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReportDto {
    private Long memberId;
    private String memberName;

    private int month;

    private int year;

    private int totalMeals;

    private BigDecimal totalDeposite;

    private BigDecimal totalExpense;

    private BigDecimal Balance;

    private BigDecimal perMealCost;
}
