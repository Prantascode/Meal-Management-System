package com.pranta.MealManagement.Controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pranta.MealManagement.Dtos.MealRequestDto;
import com.pranta.MealManagement.Dtos.MealRequestResponseDto;
import com.pranta.MealManagement.Service.MealRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/meal-requests")
@RequiredArgsConstructor
public class MealRequestController {
    
    private final MealRequestService mealRequestService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_MEMBER')")
    public MealRequestResponseDto createRequest(
            @Valid @RequestBody MealRequestDto dto,
            Principal principal
    ) {
        return mealRequestService.createMealRequest(dto, principal.getName());
    }
}
