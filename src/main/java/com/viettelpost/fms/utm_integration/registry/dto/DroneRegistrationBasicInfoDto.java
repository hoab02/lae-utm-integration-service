package com.viettelpost.fms.utm_integration.registry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public class DroneRegistrationBasicInfoDto {

    private String manufacturer;

    private String model;

    @JsonProperty("serial_number")
    private String serialNumber;

    @JsonProperty("registration_id")
    private String registrationId;

    private String type;

    private String standard;

    @JsonProperty("factory_number")
    private String factoryNumber;

    @JsonProperty("registration_issued_at")
    private Long registrationIssuedAt;

    @JsonProperty("engine_type")
    private String engineType;

    @JsonProperty("radio_work_info")
    private String radioWorkInfo;

    @JsonProperty("control_method")
    private String controlMethod;

    @JsonProperty("propose_usage")
    private String proposeUsage;
}
