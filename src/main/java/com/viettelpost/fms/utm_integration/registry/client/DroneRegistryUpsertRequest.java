package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DroneRegistryUpsertRequest(
        String droneStatus,
        String image,
        DroneRegistryBasicInfo basicInfo,
        DroneRegistrySpec spec
) {
}