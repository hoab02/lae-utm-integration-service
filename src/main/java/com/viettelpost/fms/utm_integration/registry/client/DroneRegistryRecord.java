package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DroneRegistryRecord(
        String id,
        String gcsId,
        Integer droneStatus,
        Integer statusLegit,
        String statusLegitNote,
        Boolean isReal,
        String personalIdNumber,
        String personalIdType,
        String organizationId,
        String organizationName,
        String image,
        DroneRegistryBasicInfoRecord basicInfo,
        DroneRegistrySpecRecord spec,
        Long createdAt,
        Long updatedAt,
        String createdBy,
        String updatedBy,
        String pilotId
) {
}