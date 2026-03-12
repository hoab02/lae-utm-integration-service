package com.viettelpost.fms.utm_integration.session.dto;

import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import lombok.Builder;

@Builder
public record UtmSessionContextDto(
        String sessionId,
        String token,
        SessionStatus status
) {
}