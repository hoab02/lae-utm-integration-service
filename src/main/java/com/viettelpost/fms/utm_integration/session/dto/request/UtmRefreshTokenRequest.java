package com.viettelpost.fms.utm_integration.session.dto.request;

public record UtmRefreshTokenRequest(
        String grantType,
        String refreshToken
) {
}
