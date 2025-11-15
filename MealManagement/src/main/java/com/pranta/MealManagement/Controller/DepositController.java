package com.pranta.MealManagement.Controller;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.DepositDto;
import com.pranta.MealManagement.Service.DepositService;

@RestController
@RequestMapping("/api/deposit")
@CrossOrigin("*")
public class DepositController {
    
    @Autowired
    private DepositService depositService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/add")
    public ResponseEntity<DepositDto> addDeposit(@RequestBody DepositDto dto){
        try{
            DepositDto deposit = depositService.addDeposit(dto.getMemberId(), dto.getAmount(), dto.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<DepositDto>> findDepositByMember(@PathVariable Long memberId){
        try{
            List<DepositDto> deposit = depositService.getDepositByMember(memberId);
            return ResponseEntity.ok(deposit);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/total/{memberId}/range")
    public ResponseEntity<BigDecimal> findTotalDepositByMemberAndDateRange(@PathVariable Long memberId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate){

        BigDecimal totalDeposit = depositService.getDepositByMemberAndDateRange(memberId, startDate, endDate);
        return ResponseEntity.ok(totalDeposit);
    }

    @GetMapping("/total/range")
    public ResponseEntity<List<DepositDto>> findDepositsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate
    ){
        List<DepositDto> deposits = depositService.getDepositsByDateRange(startDate, endDate);
        return ResponseEntity.ok(deposits);
    }
}
