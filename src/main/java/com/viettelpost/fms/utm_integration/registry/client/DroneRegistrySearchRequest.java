package com.viettelpost.fms.utm_integration.registry.client;

public record DroneRegistrySearchRequest(
        String serialNumber,
        String registrationId
) {
}