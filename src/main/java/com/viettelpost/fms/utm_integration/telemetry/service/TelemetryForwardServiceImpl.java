package com.viettelpost.fms.utm_integration.telemetry.service;

import com.viettelpost.fms.utm_integration.telemetry.client.UtmTelemetryPublisher;
import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryForwardServiceImpl implements TelemetryForwardService {

    private final UtmTelemetryPublisher utmTelemetryPublisher;
    private final Validator validator;
    private final MeterRegistry meterRegistry;

    @Override
    public void forward(TelemetryMessage message) {
        validate(message);

        utmTelemetryPublisher.publish(message);

        meterRegistry.counter("utm.telemetry.forward.success").increment();

        log.info("telemetry_forward_success droneId={} tenantId={} recordedAt={}",
                message.getDroneId(),
                message.getTenantId(),
                message.getRecordedAt());
    }

    private void validate(TelemetryMessage message) {
        if (message == null) {
            meterRegistry.counter("utm.telemetry.forward.invalid").increment();
            throw new IllegalArgumentException("Telemetry message must not be null");
        }

        Set<ConstraintViolation<TelemetryMessage>> violations = validator.validate(message);
        if (!violations.isEmpty()) {
            meterRegistry.counter("utm.telemetry.forward.invalid").increment();

            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            throw new IllegalArgumentException("Invalid telemetry message: " + errorMessage);
        }
    }
}