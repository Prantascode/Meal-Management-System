package com.pranta.MealManagement.Dtos;

import com.pranta.MealManagement.Entity.MealEntry.MealType;
import com.pranta.MealManagement.Enum.MealRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MealRequestResponseDto {

    private Long id;

    private Long memberId;
    private String memberName;

    private Long messId;

    private LocalDate date;
    private MealType mealType;
    private Integer mealCount;

    private MealRequestStatus status;

    private String note;

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;

    private String reviewedByName;
}