package com.viettelpost.fms.utm_integration.session.dto;

public record UtmSessionClientDisconnectRequest(
        String sessionId,
        String token
) {
}
