package com.pranta.MealManagement.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.MealEntryDto;
import com.pranta.MealManagement.Service.MealService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/meals")
public class MealController {
    
    @Autowired
    private MealService mealService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/add")
    public ResponseEntity<MealEntryDto> addMealEntry(@Valid @RequestBody MealEntryDto mealEntryDto){
        try{
            MealEntryDto createEntry = mealService.addMealEntry(mealEntryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createEntry);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MealEntryDto>> getMealEntries(
        @RequestParam @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE) LocalDate endDate){
        List<MealEntryDto> meals = mealService.getMealEntriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<MealEntryDto>> getMealEntriesByMember(@PathVariable Long memberId,
        @RequestParam @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE) LocalDate endDate){
        try{
            List<MealEntryDto> meals  = mealService.getMealEntriesByMemberAndDateRange(memberId, startDate, endDate);
            return ResponseEntity.ok(meals);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalMealEntries(
        @RequestParam @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso =  DateTimeFormat.ISO.DATE) LocalDate endDate){
            Integer totalMeals = mealService.getTotalMealsByDateRange(startDate, endDate);
            return ResponseEntity.ok(totalMeals);
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<MealEntryDto> updateMealEntry(@PathVariable Long id, @Valid @RequestBody MealEntryDto mealEntryDto){
        try{
            MealEntryDto updateEntry = mealService.updateMealEntry(id, mealEntryDto);
            return ResponseEntity.ok(updateEntry);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealEntry(@PathVariable Long id){
        try{
            mealService.deleteMealEntry(id);
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
