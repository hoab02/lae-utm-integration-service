package com.viettelpost.fms.utm_integration.approval.repository;

import com.viettelpost.fms.utm_integration.approval.domain.FlightApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlightApprovalRepository extends JpaRepository<FlightApprovalEntity, String> {

    Optional<FlightApprovalEntity> findByPlanId(String planId);

    Optional<FlightApprovalEntity> findByUtmApplicationId(String utmApplicationId);

    Optional<FlightApprovalEntity> findByUtmRequestId(String utmRequestId);

    boolean existsByPlanId(String planId);
}