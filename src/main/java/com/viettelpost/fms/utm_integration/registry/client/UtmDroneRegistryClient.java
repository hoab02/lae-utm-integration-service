package com.viettelpost.fms.utm_integration.registry.client;

public interface UtmDroneRegistryClient {

    DroneRegistrySubmissionResult submit(DroneRegistrySubmissionRequest request);
}
