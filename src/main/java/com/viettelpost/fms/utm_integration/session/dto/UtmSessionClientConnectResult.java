package com.viettelpost.fms.utm_integration.session.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record UtmSessionClientConnectResult(
        String sessionId,
        String token,
        Date connectedAt,
        Date expiresAt
) {
}
