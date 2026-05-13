package com.pranta.MealManagement.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    // User can see own requests
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_MEMBER')")
    public List<MealRequestResponseDto> getMyRequests(Principal principal) {
        return mealRequestService.getMyRequests(principal.getName());
    }

    // Admin/Manager can see pending requests of their mess
    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public List<MealRequestResponseDto> getPendingRequests(Principal principal) {
        return mealRequestService.getPendingRequests(principal.getName());
    }

    // Admin/Manager approve
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public MealRequestResponseDto approveRequest(
            @PathVariable Long id,
            Principal principal
    ) {
        return mealRequestService.approveRequest(id, principal.getName());
    }

    // Admin/Manager reject
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public MealRequestResponseDto rejectRequest(
            @PathVariable Long id,
            Principal principal
    ) {
        return mealRequestService.rejectRequest(id, principal.getName());
    }
}
