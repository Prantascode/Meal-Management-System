package com.pranta.MealManagement.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pranta.MealManagement.Dtos.MonthlyReportDto;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;
import com.pranta.MealManagement.Entity.MonthlyReport;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.MessRepository;
import com.pranta.MealManagement.Repository.MonthLyReportRepository;

@Service
public class ReportService {

    @Autowired
    private MonthLyReportRepository monthLyReportRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MessRepository messRepository; 
    @Autowired
    private MealService mealService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private DepositService depositService;

    @Transactional
    public List<MonthlyReportDto> generateMonthlyReport(int month, int year, Long messId) {
        // 1. Fetch the specific Mess
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        // 2. Fetch ONLY active members of THIS mess
        List<Member> activeMembers = memberRepository.findActiveMembersByMess(mess);
        List<MonthlyReportDto> reports = new ArrayList<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 3. Calculate total expense and meals ONLY for THIS mess
        BigDecimal totalExpense = expenseService.getTotalExpensesByMessAndDateRange(messId, startDate, endDate);
        Integer totalMeals = mealService.getTotalMealsByMessAndDateRange(messId, startDate, endDate);

        // 4. Calculate per-meal cost for THIS mess specifically
        BigDecimal perMealCost = BigDecimal.ZERO;
        if (totalMeals != null && totalMeals > 0 && totalExpense != null && totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            perMealCost = totalExpense.divide(BigDecimal.valueOf(totalMeals), 4, RoundingMode.HALF_UP);
        }

        for (Member member : activeMembers) {
            MonthlyReportDto reportDto = new MonthlyReportDto();
            reportDto.setMemberId(member.getId());
            reportDto.setMemberName(member.getName());
            reportDto.setMonth(month);
            reportDto.setYear(year);
            reportDto.setMessId(messId);
            reportDto.setPerMealCost(perMealCost.setScale(2, RoundingMode.HALF_UP));

            Integer memberMeals = mealService.getTotalMealsByMemberAndMessAndDateRange(member.getId(), messId, startDate, endDate);
            reportDto.setTotalMeals(memberMeals != null ? memberMeals : 0);

            BigDecimal memberDeposits = depositService.getDepositByMemberAndMessAndDateRange(member.getId(), messId, startDateTime, endDateTime);
            reportDto.setTotalDeposite(memberDeposits != null ? memberDeposits : BigDecimal.ZERO);

            BigDecimal memberExpense = perMealCost.multiply(BigDecimal.valueOf(reportDto.getTotalMeals()))
                                                  .setScale(2, RoundingMode.HALF_UP);
            reportDto.setTotalExpense(memberExpense);
            reportDto.setBalance(reportDto.getTotalDeposite().subtract(memberExpense));

            reports.add(reportDto);

            saveMonthlyReports(member, mess, reportDto);
        }
        return reports;
    }

    private void saveMonthlyReports(Member member, Mess mess, MonthlyReportDto reportDto) {
        MonthlyReport report = monthLyReportRepository
                .findByMemberAndMessAndMonthAndYear(member, mess, reportDto.getMonth(), reportDto.getYear())
                .orElse(new MonthlyReport(member, reportDto.getMonth(), reportDto.getYear()));

        report.setMess(mess); 
        report.setTotalMeals(reportDto.getTotalMeals());
        report.setTotalDeposite(reportDto.getTotalDeposite());
        report.setTotalExpense(reportDto.getTotalExpense());
        report.setBalance(reportDto.getBalance());
        report.setPerMealCost(reportDto.getPerMealCost());

        monthLyReportRepository.save(report);
    }

    public List<MonthlyReportDto> getMonthlyReport(int month, int year, Long messId) {
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        List<MonthlyReport> reports = monthLyReportRepository.findByMessAndMonthAndYear(mess, month, year);
        return reports.stream()
                .map(this::convertToDto)
                .toList();
    }

    private MonthlyReportDto convertToDto(MonthlyReport report) {
        MonthlyReportDto dto = new MonthlyReportDto();
        dto.setMemberId(report.getMember().getId());
        dto.setMemberName(report.getMember().getName());
        dto.setMonth(report.getMonth());
        dto.setYear(report.getYear());
        dto.setMessId(report.getMess().getId());
        dto.setTotalMeals(report.getTotalMeals());
        dto.setTotalDeposite(report.getTotalDeposite());
        dto.setTotalExpense(report.getTotalExpense());
        dto.setBalance(report.getBalance());
        dto.setPerMealCost(report.getPerMealCost());
        return dto;
    }
}