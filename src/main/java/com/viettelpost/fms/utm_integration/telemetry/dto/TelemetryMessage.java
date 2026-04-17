package com.viettelpost.fms.utm_integration.telemetry.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TelemetryMessage {

    @NotBlank(message = "droneId must not be blank")
    private String droneId;

    private String droneSerial;
    private String tenantId;

    @NotNull(message = "latitude must not be null")
    private Double latitude;

    @NotNull(message = "longitude must not be null")
    private Double longitude;

    private Double altitude;
    private Double speed;
    private Double headingDeg;

    @Min(value = 0, message = "batteryPercent must be >= 0")
    @Max(value = 100, message = "batteryPercent must be <= 100")
    private Integer batteryPercent;

    private Boolean inAir;

    @NotNull(message = "recordedAt must not be null")
    private Long recordedAt;
}