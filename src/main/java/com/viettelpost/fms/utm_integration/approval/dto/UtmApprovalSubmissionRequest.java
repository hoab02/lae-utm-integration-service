package com.viettelpost.fms.utm_integration.approval.dto;

import lombok.Builder;

@Builder
public record UtmApprovalSubmissionRequest(
        String sessionId,
        String token,
        String planId,
        String missionId,
        String droneId,
        String pilotId
) {
}