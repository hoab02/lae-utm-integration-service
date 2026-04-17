package com.viettelpost.fms.utm_integration.telemetry.mapper;

import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;
import com.viettelpost.fms.utm_integration.telemetry.redis.dto.DroneLiveStateRedisMessage;
import org.springframework.stereotype.Component;

@Component
public class DroneLiveStateTelemetryMapper {

    public TelemetryMessage toTelemetryMessage(DroneLiveStateRedisMessage source) {
        if (source == null) {
            return null;
        }

        return TelemetryMessage.builder()
                .droneId(source.getDroneId())
                .droneSerial(source.getDroneSerial())
                .tenantId(source.getTenantId())
                .latitude(source.getLatCur())
                .longitude(source.getLonCur())
                .altitude(source.getAltAbs())
                .speed(source.getVelocity())
                .headingDeg(source.getHeadingDeg())
                .batteryPercent(source.getBatteryPercent())
                .inAir(source.getInAir())
                .recordedAt(resolveRecordedAt(source))
                .build();
    }

    private Long resolveRecordedAt(DroneLiveStateRedisMessage source) {
        if (source.getLatestTimestamp() != null) {
            return source.getLatestTimestamp();
        }
        if (source.getTelemetryTimestamp() != null) {
            return source.getTelemetryTimestamp();
        }
        if (source.getStatusTimestamp() != null) {
            return source.getStatusTimestamp();
        }
        return System.currentTimeMillis();
    }
}