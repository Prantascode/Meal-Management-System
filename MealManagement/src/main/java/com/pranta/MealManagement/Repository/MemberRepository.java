package com.pranta.MealManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    List<Member> findByMessAndActiveTrue(Mess mess);
    
    @Query("SELECT m FROM Member m WHERE m.mess = :mess AND m.active = true ORDER BY m.name")
    List<Member> findActiveMembersByMess(@Param("mess") Mess mess);
    
    boolean existsByEmail(String email);
    
    Optional<Member> findByIdAndMess(Long id, Mess mess);
}