package com.pranta.MealManagement.Dtos;



import com.pranta.MealManagement.Entity.Member.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRegisterDto {
    private String name;
    private String email;
    private String password;
    private Role role;
    private String messName;
}
