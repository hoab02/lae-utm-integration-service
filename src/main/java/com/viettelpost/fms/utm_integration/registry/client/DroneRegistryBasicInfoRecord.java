package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DroneRegistryBasicInfoRecord(
        String manufacturer,
        String model,
        String serialNumber,
        String registrationId,
        String type,
        String standard,
        String factoryNumber,
        Long registrationIssuedAt,
        String engineType,
        String radioWorkInfo,
        String controlMethod,
        String proposeUsage,
        Double massKg,
        Double maxPayloadKg,
        String mode3a
) {
}