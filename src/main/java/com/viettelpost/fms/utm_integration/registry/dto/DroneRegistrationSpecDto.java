package com.viettelpost.fms.utm_integration.registry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DroneRegistrationSpecDto {

    @JsonProperty("mass_kg")
    private Double massKg;

    @JsonProperty("max_payload_kg")
    private Double maxPayloadKg;

    @JsonProperty("max_battery_capacity")
    private Integer maxBatteryCapacity;

    @JsonProperty("max_power_w")
    private Double maxPowerW;

    @JsonProperty("max_speed_mps")
    private Double maxSpeedMps;

    @JsonProperty("size_m")
    private List<Double> sizeM;

    @JsonProperty("max_takeoff_weight_kg")
    private Double maxTakeoffWeightKg;

    @JsonProperty("time_for_once_fly")
    private Integer timeForOnceFly;
}
