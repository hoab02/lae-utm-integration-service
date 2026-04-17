package com.viettelpost.fms.utm_integration.registry.client;

public record PilotRegistrySearchRequest(
        String personalIdNumber,
        String licenseNumber,
        String phoneNumber
) {
}