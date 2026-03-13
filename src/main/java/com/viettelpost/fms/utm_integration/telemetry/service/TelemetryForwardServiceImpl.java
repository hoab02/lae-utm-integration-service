package com.viettelpost.fms.utm_integration.telemetry.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import com.viettelpost.fms.utm_integration.telemetry.client.UtmTelemetryPublisher;
import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;
import com.viettelpost.fms.utm_integration.telemetry.dto.UtmTelemetryPublishRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TelemetryForwardServiceImpl implements TelemetryForwardService {

    private final UtmTelemetryPublisher utmTelemetryPublisher;
    private final UtmSessionService utmSessionService;

    @Override
    public void forward(TelemetryMessage message) throws InternalException {
        UtmSessionContextDto session = requireConnectedSession();
        utmTelemetryPublisher.publish(UtmTelemetryPublishRequest.builder()
                .sessionId(session.sessionId())
                .token(session.token())
                .missionId(message.getMissionId())
                .droneId(message.getDroneId())
                .latitude(message.getLatitude())
                .longitude(message.getLongitude())
                .altitude(message.getAltitude())
                .speed(message.getSpeed())
                .recordedAt(message.getRecordedAt() != null ? message.getRecordedAt() : new Date())
                .build());
    }

    private UtmSessionContextDto requireConnectedSession() throws InternalException {
        UtmSessionContextDto sessionContext = utmSessionService.getCurrentSessionContext();
        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
        return sessionContext;
    }
}
