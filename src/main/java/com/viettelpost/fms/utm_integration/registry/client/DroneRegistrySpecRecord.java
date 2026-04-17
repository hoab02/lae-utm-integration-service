package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DroneRegistrySpecRecord(
        Double massKg,
        Double maxPayloadKg,
        Integer maxBatteryCapacity,
        Double maxPowerW,
        Double maxSpeedMps,
        List<Double> sizeM,
        Double maxTakeoffWeightKg,
        Integer timeForOnceFly
) {
}