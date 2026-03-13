package com.viettelpost.fms.utm_integration.registry.client;

public record PilotRegistrySubmissionRequest(
        String sessionId,
        String token,
        String pilotId
) {
}
