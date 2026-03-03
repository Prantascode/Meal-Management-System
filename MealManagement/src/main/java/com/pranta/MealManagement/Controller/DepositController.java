package com.pranta.MealManagement.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pranta.MealManagement.Dtos.DepositDto;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Service.DepositService;

@RestController
@RequestMapping("/api/deposit")
@CrossOrigin(origins = "http://localhost:5173")
public class DepositController {
    
    @Autowired
    private DepositService depositService;

    @Autowired
    private MemberRepository memberRepository;

    private Long getMessIdFromPrincipal(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        return memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getMess().getId();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @PostMapping("/add")
    public ResponseEntity<DepositDto> addDeposit(@RequestBody DepositDto dto, Principal principal){
        try{
            Long messId = getMessIdFromPrincipal(principal);
            DepositDto deposit = depositService.addDeposit(dto.getMemberId(), messId, dto.getAmount(), dto.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<DepositDto>> findDepositByMember(@PathVariable Long memberId, Principal principal){
        try{
            Long messId = getMessIdFromPrincipal(principal);
            List<DepositDto> deposit = depositService.getDepositByMemberAndMess(memberId, messId);
            return ResponseEntity.ok(deposit);
        } catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/total/{memberId}/range")
    public ResponseEntity<BigDecimal> findTotalDepositByMemberAndDateRange(
        @PathVariable Long memberId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Principal principal){

        Long messId = getMessIdFromPrincipal(principal);
        BigDecimal totalDeposit = depositService.getDepositByMemberAndMessAndDateRange(memberId, messId, startDate, endDate);
        return ResponseEntity.ok(totalDeposit);
    }

    @GetMapping("/total/range")
    public ResponseEntity<List<DepositDto>> findDepositsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Principal principal
    ){
        Long messId = getMessIdFromPrincipal(principal);
        List<DepositDto> deposits = depositService.getDepositsByMessAndDateRange(messId, startDate, endDate);
        return ResponseEntity.ok(deposits);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<DepositDto> updateDeposit(@PathVariable Long id, @RequestBody DepositDto dto) {
        try {
            DepositDto updated = depositService.updateDeposit(id, dto); 
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeposit(@PathVariable Long id) {
        try {
            depositService.deleteDeposit(id); 
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}