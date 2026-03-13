package com.viettelpost.fms.utm_integration.mission.dto;

import lombok.Builder;

@Builder
public record UtmMissionEventRequest(
        String sessionId,
        String token,
        String missionId,
        String planId,
        String droneId,
        String emergencyReason
) {
}