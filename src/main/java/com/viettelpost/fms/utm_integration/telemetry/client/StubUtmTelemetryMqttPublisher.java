package com.viettelpost.fms.utm_integration.telemetry.client;

import com.viettelpost.fms.utm_integration.telemetry.dto.UtmTelemetryPublishRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StubUtmTelemetryMqttPublisher implements UtmTelemetryPublisher {

    @Override
    public void publish(UtmTelemetryPublishRequest request) {
        log.info("Stub telemetry publish for missionId={}, droneId={}", request.getMissionId(), request.getDroneId());
    }
}
