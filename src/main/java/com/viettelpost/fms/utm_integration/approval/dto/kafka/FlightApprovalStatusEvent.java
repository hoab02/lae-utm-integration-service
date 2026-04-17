package com.viettelpost.fms.utm_integration.approval.dto.kafka;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FlightApprovalStatusEvent {

    private String flightTripCode;
    private String missionId;
    private String utmApplicationId;
    private String utmRequestId;
    private ApprovalStatus status;
    private String reason;
    private Instant submittedAt;
    private Instant approvedAt;
    private Instant rejectedAt;
    private Instant cancelledAt;
    private Instant eventTime;
}