package com.viettelpost.fms.utm_integration.registry.client;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StubUtmDroneRegistryClient implements UtmDroneRegistryClient {

    @Override
    public DroneRegistrySubmissionResult submit(DroneRegistrySubmissionRequest request) {
        return new DroneRegistrySubmissionResult("utm-drone-" + UUID.randomUUID());
    }
}
