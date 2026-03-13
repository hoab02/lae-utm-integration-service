package com.viettelpost.fms.utm_integration.airspace.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateStatus;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
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
public class AirspaceUpdateStatusDto {

    private String id;

    private String updateId;

    private AirspaceUpdateType type;

    private String version;

    private String payload;

    private Date effectiveFrom;

    private Date receivedAt;

    private String source;

    private AirspaceUpdateStatus status;
}
