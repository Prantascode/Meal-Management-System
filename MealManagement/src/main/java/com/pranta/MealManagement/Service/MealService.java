package com.pranta.MealManagement.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.MealEntryDto;
import com.pranta.MealManagement.Entity.MealEntry;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;
import com.pranta.MealManagement.Repository.MealEntryRepository;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.MessRepository;

@Service
public class MealService {

    @Autowired
    private MealEntryRepository mealEntryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessRepository messRepository;

    public MealEntryDto addMealEntry(MealEntryDto mealEntryDto) {
        Member member = memberRepository.findById(mealEntryDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        Mess mess = messRepository.findById(mealEntryDto.getMessId())
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        MealEntry mealEntry = new MealEntry();
        mealEntry.setMember(member);
        mealEntry.setMess(mess); 
        mealEntry.setDate(mealEntryDto.getDate());
        mealEntry.setMealType(mealEntryDto.getMealType());
        mealEntry.setMealCount(mealEntryDto.getMealCount());

        MealEntry savedEntry = mealEntryRepository.save(mealEntry);
        return convertToDto(savedEntry);
    }

    public List<MealEntryDto> getMealEntriesByMessAndDateRange(Long messId, LocalDate startDate, LocalDate endDate) {
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        return mealEntryRepository.findByMessAndDateBetweenOrderByDateDesc(mess, startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<MealEntryDto> getMealEntriesByMemberAndMessAndDateRange(Long memberId, Long messId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        return mealEntryRepository.findByMemberAndMessAndDateBetween(member, mess, startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Integer getTotalMealsByMessAndDateRange(Long messId, LocalDate startDate, LocalDate endDate) {
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        Integer total = mealEntryRepository.getTotalMealsByMessBetweenDates(mess, startDate, endDate);
        return total != null ? total : 0;
    }

    public Integer getTotalMealsByMemberAndMessAndDateRange(Long memberId, Long messId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        Integer total = mealEntryRepository.getTotalMealsByMemberAndMessBetweenDates(member, mess, startDate, endDate);
        return total != null ? total : 0;
    }

    public MealEntryDto updateMealEntry(Long id, MealEntryDto mealEntryDto) {
        MealEntry mealEntry = mealEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal Entry not found"));

        mealEntry.setDate(mealEntryDto.getDate());
        mealEntry.setMealCount(mealEntryDto.getMealCount());
        mealEntry.setMealType(mealEntryDto.getMealType());

        MealEntry updateMealEntry = mealEntryRepository.save(mealEntry);
        return convertToDto(updateMealEntry);
    }

    public void deleteMealEntry(Long id) {
        MealEntry mealEntry = mealEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal Entry not found"));
        mealEntryRepository.delete(mealEntry);
    }

    private MealEntryDto convertToDto(MealEntry mealEntry) {
        MealEntryDto dto = new MealEntryDto();
        dto.setId(mealEntry.getId());
        dto.setMemberId(mealEntry.getMember().getId());
        dto.setMemberName(mealEntry.getMember().getName());
        dto.setMessId(mealEntry.getMess().getId()); 
        dto.setDate(mealEntry.getDate());
        dto.setMealType(mealEntry.getMealType());
        dto.setMealCount(mealEntry.getMealCount());
        return dto;
    }
}