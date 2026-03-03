package com.pranta.MealManagement.Dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositDto {
    private Long id;
    private Long memberId;
    private String memberName;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    private Long messId;
}
