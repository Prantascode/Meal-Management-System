package com.pranta.MealManagement.Dtos;


import com.pranta.MealManagement.Entity.Member;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Invalid email formate")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    //@NotBlank(message = "Phone number is required")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Member.Role role;

    private boolean active = true;

    private String messName;
}
