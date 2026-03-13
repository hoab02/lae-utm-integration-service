package com.viettelpost.fms.utm_integration.command.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
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
public class UtmCommandMessage {

    @NotBlank(message = "command_id is required")
    private String commandId;

    private String missionId;

    @NotBlank(message = "command_type is required")
    private String commandType;

    private Integer priority;

    private String payload;

    private Date receivedAt;
}
