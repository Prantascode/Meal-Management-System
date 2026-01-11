package com.pranta.MealManagement.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.MonthlyReportDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.MonthlyReport;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.MonthLyReportRepository;

@Service
public class ReportService {
    
    @Autowired
    private MonthLyReportRepository monthLyReportRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MealService mealService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private DepositService depositService;


    public List<MonthlyReportDto> generateMonthlyReport(int month,int year){
        List<Member> activeMembers = memberRepository.findActiveMembers();
        List<MonthlyReportDto> reports = new ArrayList<>();

        //Calculate Date Range
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        //Get total expense and meal for the month
        BigDecimal totalExpense = expenseService.getTotalExpensesByDateRange(startDate, endDate);
        Integer totalMeals = mealService.getTotalMealsByDateRange(startDate, endDate);

        //Calculate per-meal cost
        BigDecimal perMealCost = BigDecimal.ZERO;
        if (totalMeals > 0 && totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            perMealCost = totalExpense.divide(BigDecimal.valueOf(totalMeals), 2, RoundingMode.HALF_UP);
        }

        for (Member member : activeMembers) {
            MonthlyReportDto reportDto = new MonthlyReportDto();
            reportDto.setMemberId(member.getId());
            reportDto.setMemberName(member.getName());
            reportDto.setMonth(month);
            reportDto.setYear(year);
            reportDto.setPerMealCost(perMealCost);

            //Get Member's Meal count
            Integer memberMeals = mealService.getTotalMealsByMemberAndDateRange(member.getId(),startDate, endDate);           
            reportDto.setTotalMeals(memberMeals != null ? memberMeals : 0);

            //Get Member's Deposits
            BigDecimal memberDeposits = depositService.getDepositByMemberAndDateRange(member.getId(), startDateTime, endDateTime);
            reportDto.setTotalDeposite(memberDeposits);

            //Calculate member's expense
            BigDecimal memberExpense = perMealCost.multiply(BigDecimal.valueOf(reportDto.getTotalMeals()));
            reportDto.setTotalExpense(memberExpense);

            //Calculate Balance
            BigDecimal balance = memberDeposits.subtract(memberExpense);
            reportDto.setBalance(balance);

            reports.add(reportDto);

            saveMonthlyReports(member, reportDto);
        
        }
        return reports;
    }

    private void saveMonthlyReports(Member member,MonthlyReportDto reportDto){
        MonthlyReport report = monthLyReportRepository
                        .findByMemberAndMonthAndYear(member, reportDto.getMonth(), reportDto.getYear())
                        .orElse(new MonthlyReport(member, reportDto.getMonth(), reportDto.getYear()));

        report.setTotalMeals(reportDto.getTotalMeals());
        report.setTotalDeposite(reportDto.getTotalDeposite());
        report.setTotalDeposite(reportDto.getTotalDeposite());
        report.setBalance(reportDto.getBalance());
        report.setPerMealCost(reportDto.getPerMealCost());

        monthLyReportRepository.save(report);
    }


    public List<MonthlyReportDto> getMonthlyReport(int month,int year){
        List<MonthlyReport> reports = monthLyReportRepository.findByMonthAndYear(month, year);
        return reports.stream()
                .map(this :: convertToDto)
                .toList();
    }


    private MonthlyReportDto convertToDto(MonthlyReport report){
        MonthlyReportDto dto = new MonthlyReportDto();
        dto.setMemberId(report.getMember().getId());
        dto.setMemberName(report.getMember().getName());
        dto.setMonth(report.getMonth());
        dto.setYear(report.getYear());
        dto.setTotalMeals(report.getTotalMeals());
        dto.setTotalDeposite(report.getTotalDeposite());
        dto.setTotalExpense(report.getTotalExpense());
        dto.setBalance(report.getBalance());
        dto.setPerMealCost(report.getPerMealCost());
        return dto;
    }
}
