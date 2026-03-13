package com.viettelpost.fms.utm_integration.command.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.command.domain.CommandStatus;
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
public class UtmCommandStatusDto {

    private String id;

    private String commandId;

    private String missionId;

    private String commandType;

    private Integer priority;

    private String payload;

    private CommandStatus status;

    private Date receivedAt;

    private Date ackAt;

    private Date executedAt;

    private String failureReason;
}
