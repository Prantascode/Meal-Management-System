package com.pranta.MealManagement.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deposits")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deposit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0",inclusive = false,message = "Amount must be positive")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "deposit_date")
    private LocalDateTime depositDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mess_id", nullable = false)
    @NotNull(message = "Mess reference is required")
    private Mess mess;
    
    private String description;
    public Deposit(Member member,BigDecimal amount,String description,Mess mess){
        this.member = member;
        this.amount = amount;
        this.description = description;
        this.depositDate = LocalDateTime.now();
        this.mess = mess;
    }
}
