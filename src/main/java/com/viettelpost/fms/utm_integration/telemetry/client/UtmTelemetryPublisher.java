package com.viettelpost.fms.utm_integration.telemetry.client;

import com.viettelpost.fms.utm_integration.telemetry.dto.UtmTelemetryPublishRequest;

public interface UtmTelemetryPublisher {

    void publish(UtmTelemetryPublishRequest request);
}
