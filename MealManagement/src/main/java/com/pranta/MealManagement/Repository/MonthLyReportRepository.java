package com.pranta.MealManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.MonthlyReport;

@Repository
public interface MonthLyReportRepository extends JpaRepository<MonthlyReport, Long> {
    Optional<MonthlyReport> findByMemberAndMonthAndYear(Member member,int month,int year);
    List<MonthlyReport> findByMonthAndYear(int month,int year);
    List<MonthlyReport> findByMemberOrderByYearDescMonthDesc(Member member);
}
