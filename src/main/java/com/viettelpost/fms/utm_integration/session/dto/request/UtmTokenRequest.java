package com.viettelpost.fms.utm_integration.session.dto.request;

public record UtmTokenRequest(
        String grantType,
        String username,
        String password
) {
}
