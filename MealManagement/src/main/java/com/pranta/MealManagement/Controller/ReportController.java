package com.pranta.MealManagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.MonthlyReportDto;
import com.pranta.MealManagement.Service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;
    
    @PostMapping("/generate/{month}/{year}")
    public ResponseEntity<List<MonthlyReportDto>> generateMonthlyReport(@PathVariable int month,@PathVariable int year) {
        List<MonthlyReportDto> reports = reportService.generateMonthlyReport(month, year);
        
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/{month}/{year}")
    public ResponseEntity<List<MonthlyReportDto>> getMonthlyReport(@PathVariable int month,@PathVariable int year) {
        List<MonthlyReportDto> reports = reportService.getMonthlyReport(month, year);
        return ResponseEntity.ok(reports);
    }
}
