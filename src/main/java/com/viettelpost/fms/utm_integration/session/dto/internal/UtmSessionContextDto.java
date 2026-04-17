package com.viettelpost.fms.utm_integration.session.dto.internal;

import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import lombok.Builder;

import java.util.Date;

@Builder
public record UtmSessionContextDto(
        String accessToken,
        String tokenType,
        SessionStatus status,
        Boolean active,
        Date accessTokenExpiresAt,
        Date refreshTokenExpiresAt
) {
}
