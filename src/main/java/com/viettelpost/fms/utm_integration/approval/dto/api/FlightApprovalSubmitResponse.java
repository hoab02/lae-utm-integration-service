package com.viettelpost.fms.utm_integration.approval.dto.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FlightApprovalSubmitResponse {
    private String id;
    private String planId;
    private String missionId;
    private String utmApplicationId;
    private String utmRequestId;
    private ApprovalStatus status;
    private Instant submittedAt;
}