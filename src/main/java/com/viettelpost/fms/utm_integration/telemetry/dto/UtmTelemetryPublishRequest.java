package com.viettelpost.fms.utm_integration.telemetry.dto;

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
public class UtmTelemetryPublishRequest {

    private String sessionId;

    private String token;

    private String missionId;

    private String droneId;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Double speed;

    private Date recordedAt;
}
