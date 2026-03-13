package com.viettelpost.fms.utm_integration.registry.client;

public interface UtmPilotRegistryClient {

    PilotRegistrySubmissionResult submit(PilotRegistrySubmissionRequest request);
}
