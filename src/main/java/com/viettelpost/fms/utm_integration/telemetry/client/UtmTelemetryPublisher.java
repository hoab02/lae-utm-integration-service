package com.viettelpost.fms.utm_integration.telemetry.client;

import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;

public interface UtmTelemetryPublisher {

    void publish(TelemetryMessage message);
}