package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DroneRegistrySearchData(
        List<DroneRegistryRecord> items,
        Integer limit,
        Integer offset,
        Integer total
) {
}