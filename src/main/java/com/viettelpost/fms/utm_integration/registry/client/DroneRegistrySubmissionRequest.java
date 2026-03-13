package com.viettelpost.fms.utm_integration.registry.client;

public record DroneRegistrySubmissionRequest(
        String sessionId,
        String token,
        String droneId
) {
}
