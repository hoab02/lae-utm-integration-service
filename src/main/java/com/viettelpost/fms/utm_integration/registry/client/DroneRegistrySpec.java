package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DroneRegistrySpec(
        @JsonProperty("mass_kg") Double massKg,
        @JsonProperty("max_payload_kg") Double maxPayloadKg,
        @JsonProperty("max_battery_capacity") Integer maxBatteryCapacity,
        @JsonProperty("max_power_w") Double maxPowerW,
        @JsonProperty("max_speed_mps") Double maxSpeedMps,
        @JsonProperty("size_m") List<Double> sizeM,
        @JsonProperty("max_takeoff_weight_kg") Double maxTakeoffWeightKg,
        @JsonProperty("time_for_once_fly") Integer timeForOnceFly
) {
}
