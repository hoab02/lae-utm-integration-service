package com.viettelpost.fms.utm_integration.session.dto.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import lombok.Builder;

import java.util.Date;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UtmSessionStatusDto(
        String id,
        String dcsId,
        SessionStatus status,
        Boolean active,
        Date connectedAt,
        Date lastRefreshAt,
        Date accessTokenExpiresAt,
        Date refreshTokenExpiresAt,
        Date disconnectedAt,
        String failureReason
) {
}
