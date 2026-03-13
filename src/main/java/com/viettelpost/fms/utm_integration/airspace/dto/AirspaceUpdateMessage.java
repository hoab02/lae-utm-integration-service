package com.viettelpost.fms.utm_integration.airspace.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AirspaceUpdateMessage {

    @NotBlank(message = "update_id is required")
    private String updateId;

    @NotNull(message = "type is required")
    private AirspaceUpdateType type;

    private String version;

    @NotBlank(message = "payload is required")
    private String payload;

    private Date effectiveFrom;

    private Date receivedAt;

    private String source;
}
