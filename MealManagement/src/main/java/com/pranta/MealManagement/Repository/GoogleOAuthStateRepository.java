package com.pranta.MealManagement.Repository;

import com.pranta.MealManagement.Entity.GoogleOAuthState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleOAuthStateRepository extends JpaRepository<GoogleOAuthState, Long> {

    Optional<GoogleOAuthState> findByState(String state);

    void deleteByState(String state);
}