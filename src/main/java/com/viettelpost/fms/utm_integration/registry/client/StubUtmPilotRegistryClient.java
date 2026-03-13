package com.viettelpost.fms.utm_integration.registry.client;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StubUtmPilotRegistryClient implements UtmPilotRegistryClient {

    @Override
    public PilotRegistrySubmissionResult submit(PilotRegistrySubmissionRequest request) {
        return new PilotRegistrySubmissionResult("utm-pilot-" + UUID.randomUUID());
    }
}
