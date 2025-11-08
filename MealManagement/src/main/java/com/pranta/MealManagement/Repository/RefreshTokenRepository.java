package com.pranta.MealManagement.Repository;


import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByMember(Member member);
    
    @Modifying
    int deleteByMember(Member member);
}
