package com.pranta.MealManagement.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.DepositDto;
import com.pranta.MealManagement.Entity.Deposit;
import com.pranta.MealManagement.Service.DepositService;

@RestController
@RequestMapping("/api/deposit")
@CrossOrigin("*")
public class DepositController {
    
    @Autowired
    public DepositService depositService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/add")
    public ResponseEntity<Deposit> addDeposit(@RequestBody DepositDto dto){
        try{
            Deposit deposit = depositService.addDeposit(dto.getMemberId(), dto.getAmount(), dto.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
