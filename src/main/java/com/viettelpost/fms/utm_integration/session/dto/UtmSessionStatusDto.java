package com.viettelpost.fms.utm_integration.session.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UtmSessionStatusDto {

    private String id;

    private String sessionId;

    private SessionStatus status;

    private Date connectedAt;

    private Date lastHeartbeatAt;

    private Date expiresAt;

    private String failureReason;
}
