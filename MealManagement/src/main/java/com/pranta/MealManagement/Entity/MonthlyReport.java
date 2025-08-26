package com.pranta.MealManagement.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monthly_report")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    @Column(name = "total_meals")
    private int totalMeals;

    @Column(name = "total_deposite",precision = 10,scale = 2)
    private BigDecimal totalDeposite;

    @Column(name = "total_expense",precision = 10,scale = 2)
    private BigDecimal totalExpense;

    @Column(name = "balance",precision = 10,scale = 2)
    private BigDecimal Balance;

    @Column(name = "per_meal_cost",precision = 10,scale = 2)
    private BigDecimal perMealCost;

    @Column(name = "generated_date")
    private LocalDate generatedDate = LocalDate.now();
}
