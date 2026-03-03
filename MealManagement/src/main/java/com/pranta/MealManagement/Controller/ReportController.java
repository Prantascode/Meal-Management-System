package com.pranta.MealManagement.Controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pranta.MealManagement.Dtos.MonthlyReportDto;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Service.ReportService;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private MemberRepository memberRepository;

    private Long getMessIdFromPrincipal(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        return memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getMess().getId();
    }
    
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @PostMapping("/generate/{month}/{year}")
    public ResponseEntity<List<MonthlyReportDto>> generateMonthlyReport(
            @PathVariable int month, 
            @PathVariable int year,
            Principal principal) {
        
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }

        Long messId = getMessIdFromPrincipal(principal);
        List<MonthlyReportDto> reports = reportService.generateMonthlyReport(month, year, messId);
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/{month}/{year}")
    public ResponseEntity<List<MonthlyReportDto>> getMonthlyReport(
            @PathVariable int month, 
            @PathVariable int year,
            Principal principal) {
        
        Long messId = getMessIdFromPrincipal(principal);
        List<MonthlyReportDto> reports = reportService.getMonthlyReport(month, year, messId);
        
        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(reports);
    }
}