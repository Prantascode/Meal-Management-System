package com.pranta.MealManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByActiveTrue();
    
    @Query("SELECT m FROM Member m WHERE m.active = true ORDER BY m.name")
    List<Member> findActiveMembers();
    
    boolean existsByEmail(String email);
}
