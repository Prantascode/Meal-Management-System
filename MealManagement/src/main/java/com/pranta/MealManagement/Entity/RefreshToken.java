package com.pranta.MealManagement.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private Instant expiryDate;
}
