package com.pranta.MealManagement.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Deposit;
import com.pranta.MealManagement.Entity.Member;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByMemberOrderByDepositDateDesc(Member member);
    
    @Query("SELECT SUM(d.amount) FROM Deposit d WHERE d.member = :member AND " +
           "d.depositDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalDepositsByMemberBetweenDates(@Param("member") Member member,
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    List<Deposit> findByDepositDateBetweenOrderByDepositDateDesc(LocalDateTime startDate, 
                                                               LocalDateTime endDate);
}
