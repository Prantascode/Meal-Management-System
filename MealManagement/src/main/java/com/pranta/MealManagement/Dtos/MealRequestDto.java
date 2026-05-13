package com.pranta.MealManagement.Dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

import com.pranta.MealManagement.Entity.MealEntry.MealType;

@Data
public class MealRequestDto {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Meal count is required")
    @Min(value = 1, message = "Meal count must be at least 1")
    private Integer mealCount;

    private String note;
}