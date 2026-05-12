package com.pranta.MealManagement.Repository;

import com.pranta.MealManagement.Entity.MealRequest;
import com.pranta.MealManagement.Enum.MealRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealRequestRepository extends JpaRepository<MealRequest, Long> {

    List<MealRequest> findByMessIdAndStatusOrderByRequestedAtDesc(
            Long messId,
            MealRequestStatus status
    );

    List<MealRequest> findByMemberIdOrderByRequestedAtDesc(Long memberId);

    List<MealRequest> findByMessIdOrderByRequestedAtDesc(Long messId);
}