package com.pranta.MealManagement.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.MealEntry;
import com.pranta.MealManagement.Entity.Member;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    List<MealEntry> findByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(me.mealCount) FROM MealEntry me WHERE me.date BETWEEN :startDate AND :endDate")
    Integer getTotalMealsBetweenDates(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(me.mealCount) FROM MealEntry me WHERE me.member = :member AND " +
           "me.date BETWEEN :startDate AND :endDate")
    Integer getTotalMealsByMemberBetweenDates(@Param("member") Member member,
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    List<MealEntry> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);
}

