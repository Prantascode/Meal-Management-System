package com.pranta.MealManagement.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.MealRequestDto;
import com.pranta.MealManagement.Dtos.MealRequestResponseDto;
import com.pranta.MealManagement.Entity.MealRequest;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Enum.MealRequestStatus;
import com.pranta.MealManagement.Repository.MealRequestRepository;
import com.pranta.MealManagement.Repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MealRequestService {
    
    private final MemberRepository memberRepository;
    private final MealRequestRepository mealRequestRepository;
    private final MealService mealService;

    public MealRequestResponseDto createMealRequest(MealRequestDto mealRequestDto,String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(""));
        
        MealRequest mealRequest = new MealRequest();
        mealRequest.setMember(member);
        mealRequest.setMess(member.getMess());
        mealRequest.setDate(mealRequestDto.getDate());
        mealRequest.setMealType(mealRequestDto.getMealType());
        mealRequest.setMealCount(mealRequestDto.getMealCount());
        mealRequest.setStatus(MealRequestStatus.PENDING);
        mealRequest.setNote(mealRequestDto.getNote());
        mealRequest.setRequestedAt(LocalDateTime.now());

        MealRequest savedRequest = mealRequestRepository.save(mealRequest);

        return mapToDto(savedRequest);
    }
    
     public List<MealRequestResponseDto> getMyRequests(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return mealRequestRepository.findByMemberIdOrderByRequestedAtDesc(member.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


     private MealRequestResponseDto mapToDto(MealRequest request) {
        return MealRequestResponseDto.builder()
                .id(request.getId())
                .memberId(request.getMember().getId())
                .memberName(request.getMember().getName())
                .messId(request.getMess().getId())
                .date(request.getDate())
                .mealType(request.getMealType())
                .mealCount(request.getMealCount())
                .status(request.getStatus())
                .note(request.getNote())
                .requestedAt(request.getRequestedAt())
                .reviewedAt(request.getReviewedAt())
                .reviewedByName(
                        request.getReviewedBy() != null
                                ? request.getReviewedBy().getName()
                                : null
                )
                .build();
    }
}
