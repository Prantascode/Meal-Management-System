package com.pranta.MealManagement.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expense")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Discription is required")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Amount is Required")
    @DecimalMin(value = "0.0",inclusive = false,message = "Amount must be positive")
    @Column(precision = 10,scale = 2,nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Category is requried")
    private ExpenseCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by",nullable = false)
    private Member addedBy;

    
    public enum ExpenseCategory{
        MEAT, VEGETABLES, OTHER
    }
}
