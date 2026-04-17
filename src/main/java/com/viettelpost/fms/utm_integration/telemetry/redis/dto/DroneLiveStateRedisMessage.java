package com.viettelpost.fms.utm_integration.telemetry.redis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DroneLiveStateRedisMessage {

    private String droneId;
    private String droneSerial;
    private String tenantId;

    private Double latCur;
    private Double lonCur;
    private Double altAbs;

    private Double headingDeg;
    private Double velocity;

    private Integer batteryPercent;
    private Boolean inAir;

    private Long telemetryTimestamp;
    private Long statusTimestamp;
    private Long latestTimestamp;
}