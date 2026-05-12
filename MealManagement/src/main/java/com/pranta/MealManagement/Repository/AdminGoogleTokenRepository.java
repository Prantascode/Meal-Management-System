package com.pranta.MealManagement.Repository;

import com.pranta.MealManagement.Entity.AdminGoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminGoogleTokenRepository extends JpaRepository<AdminGoogleToken, Long> {
    Optional<AdminGoogleToken> findByAdminEmail(String adminEmail);
}