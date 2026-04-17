package com.viettelpost.fms.utm_integration.telemetry.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;
import com.viettelpost.fms.utm_integration.telemetry.mapper.DroneLiveStateTelemetryMapper;
import com.viettelpost.fms.utm_integration.telemetry.redis.dto.DroneLiveStateRedisMessage;
import com.viettelpost.fms.utm_integration.telemetry.service.TelemetryForwardService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryRedisSubscriber {

    private final ObjectMapper objectMapper;
    private final DroneLiveStateTelemetryMapper mapper;
    private final TelemetryForwardService telemetryForwardService;
    private final MeterRegistry meterRegistry;

    public void receive(String payload) {
        try {
            DroneLiveStateRedisMessage liveState =
                    objectMapper.readValue(payload, DroneLiveStateRedisMessage.class);

            TelemetryMessage telemetryMessage = mapper.toTelemetryMessage(liveState);
            telemetryForwardService.forward(telemetryMessage);

            meterRegistry.counter("utm.telemetry.redis.receive.success").increment();
        } catch (Exception e) {
            meterRegistry.counter("utm.telemetry.redis.receive.failed").increment();
            log.error("telemetry_redis_receive_failed", e);
            throw new IllegalArgumentException("Failed to process telemetry redis payload", e);
        }
    }
}