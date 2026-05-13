package com.pranta.MealManagement.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMyProfileDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String phone;
}