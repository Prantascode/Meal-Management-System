package com.pranta.MealManagement.Entity;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Invalid email formate")
    @Column(unique = true,nullable = false)
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.MEMBER;

    @Column(name = "join_date")
    private LocalDateTime joinDate = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean active = true;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<MealEntity> mealEntities;
    
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Deposit> deposits;

    public enum Role{
        ADMIN,MANAGER,MEMBER
    }
}
