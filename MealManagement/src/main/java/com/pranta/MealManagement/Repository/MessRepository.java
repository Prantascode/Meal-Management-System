package com.pranta.MealManagement.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Mess;

@Repository
public interface MessRepository extends JpaRepository<Mess, Long>{
    Optional<Mess> findByMessName(String messName);
}
