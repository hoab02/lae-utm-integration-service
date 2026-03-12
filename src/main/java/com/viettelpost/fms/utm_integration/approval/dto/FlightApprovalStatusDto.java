package com.viettelpost.fms.utm_integration.approval.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FlightApprovalStatusDto {

    private String id;

    private String planId;

    private String missionId;

    private String droneId;

    private String pilotId;

    private String utmRequestId;

    private ApprovalStatus status;

    private Date requestedAt;

    private Date approvedAt;

    private Date rejectedAt;

    private String rejectReason;
}