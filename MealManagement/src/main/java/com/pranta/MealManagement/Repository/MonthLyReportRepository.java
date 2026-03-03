package com.pranta.MealManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.MonthlyReport;
import com.pranta.MealManagement.Entity.Mess;

@Repository
public interface MonthLyReportRepository extends JpaRepository<MonthlyReport, Long> {

    @Query("SELECT r FROM MonthlyReport r WHERE r.member = :member AND r.mess = :mess AND r.month = :month AND r.year = :year")
    Optional<MonthlyReport> findByMemberAndMessAndMonthAndYear(
        @Param("member") Member member, 
        @Param("mess") Mess mess, 
        @Param("month") int month, 
        @Param("year") int year
    );

    List<MonthlyReport> findByMessAndMonthAndYear(Mess mess, int month, int year);

    List<MonthlyReport> findByMemberAndMessOrderByYearDescMonthDesc(Member member, Mess mess);
}