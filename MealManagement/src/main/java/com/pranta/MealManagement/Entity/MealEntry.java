package com.pranta.MealManagement.Entity;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "male_entry")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @NotNull(message = "Date is required")
    @Column(name = "date")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Meal Type is required")
    private MealType mealType;

    @Column(name = "meal_count")
    private int mealCount = 1;
    public enum MealType{
        BREAKFAST, LUNCH, DINNER
    } 
}
