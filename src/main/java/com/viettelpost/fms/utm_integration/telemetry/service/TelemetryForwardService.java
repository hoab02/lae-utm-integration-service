package com.viettelpost.fms.utm_integration.telemetry.service;

import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;

public interface TelemetryForwardService {

    void forward(TelemetryMessage message);
}