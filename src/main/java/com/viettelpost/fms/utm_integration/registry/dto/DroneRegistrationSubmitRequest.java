package com.viettelpost.fms.utm_integration.registry.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DroneRegistrationSubmitRequest {

    @NotNull(message = "drone_status is required")
    private Integer droneStatus;

    private String image;

    @Valid
    @NotNull(message = "basic_info is required")
    private DroneRegistrationBasicInfoDto basicInfo;

    @Valid
    @NotNull(message = "spec is required")
    private DroneRegistrationSpecDto spec;
}
