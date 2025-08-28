package com.pranta.MealManagement.Dtos;

import java.time.LocalDate;

import com.pranta.MealManagement.Entity.MealEntry;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealEntryDto {
    private Long id;

    @NotNull(message = "Member Id is required")
    private Long memberId;
    private String memberName;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Meal Type is required")
    private MealEntry.MealType mealType;
    
    @Min(value = 1,message = "meal count must be at least 1")
    private int mealCount = 1;
}
