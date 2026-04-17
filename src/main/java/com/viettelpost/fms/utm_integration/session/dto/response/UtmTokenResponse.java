package com.viettelpost.fms.utm_integration.session.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UtmTokenResponse(
        String accessToken,
        Long expiresIn,
        String refreshToken,
        Long refreshExpiresIn,
        String tokenType,
        String idToken,
        String sessionState,
        String scope
) {
}
