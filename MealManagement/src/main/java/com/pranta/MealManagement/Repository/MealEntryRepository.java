package com.pranta.MealManagement.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.MealEntry;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    List<MealEntry> findByMemberAndMessAndDateBetween(Member member, Mess mess, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(me.mealCount) FROM MealEntry me WHERE me.mess = :mess AND me.date BETWEEN :startDate AND :endDate")
    Integer getTotalMealsByMessBetweenDates(@Param("mess") Mess mess, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(me.mealCount) FROM MealEntry me WHERE me.member = :member AND me.mess = :mess AND " +
           "me.date BETWEEN :startDate AND :endDate")
    Integer getTotalMealsByMemberAndMessBetweenDates(@Param("member") Member member,
                                                     @Param("mess") Mess mess,
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
    List<MealEntry> findByMessAndDateBetweenOrderByDateDesc(Mess mess, LocalDate startDate, LocalDate endDate);
}