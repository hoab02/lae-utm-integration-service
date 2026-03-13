package com.viettelpost.fms.utm_integration.mission.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.mission.domain.MissionState;
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
public class MissionStatusDto {

    private String id;

    private String missionId;

    private String planId;

    private String droneId;

    private MissionState state;

    private Date airborneAt;

    private Date landingAt;

    private Date completedAt;

    private boolean emergencyFlag;

    private String emergencyReason;
}