package com.viettelpost.fms.utm_integration.telemetry.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import com.viettelpost.fms.utm_integration.telemetry.client.UtmTelemetryPublisher;
import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;
import com.viettelpost.fms.utm_integration.telemetry.dto.UtmTelemetryPublishRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetryForwardServiceImplTest {

    @Mock
    private UtmTelemetryPublisher utmTelemetryPublisher;

    @Mock
    private UtmSessionService utmSessionService;

    @InjectMocks
    private TelemetryForwardServiceImpl telemetryForwardService;

    @Test
    void forwardShouldPublishMappedTelemetryWhenSessionIsConnected() throws InternalException {
        Date recordedAt = new Date();
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());

        telemetryForwardService.forward(TelemetryMessage.builder()
                .missionId("mission-1")
                .droneId("drone-1")
                .latitude(10.5)
                .longitude(106.7)
                .altitude(120.0)
                .speed(14.5)
                .recordedAt(recordedAt)
                .build());

        ArgumentCaptor<UtmTelemetryPublishRequest> captor = ArgumentCaptor.forClass(UtmTelemetryPublishRequest.class);
        verify(utmTelemetryPublisher).publish(captor.capture());
        assertEquals("session-1", captor.getValue().getSessionId());
        assertEquals("token-1", captor.getValue().getToken());
        assertEquals("mission-1", captor.getValue().getMissionId());
        assertEquals(recordedAt, captor.getValue().getRecordedAt());
    }

    @Test
    void forwardShouldRejectWhenSessionIsNotConnected() {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .status(SessionStatus.DISCONNECTED)
                .build());

        InternalException ex = assertThrows(InternalException.class, () -> telemetryForwardService.forward(TelemetryMessage.builder()
                .missionId("mission-1")
                .droneId("drone-1")
                .latitude(10.5)
                .longitude(106.7)
                .build()));

        assertEquals(ErrorCode.ERROR_SESSION_NOT_CONNECTED.name(), ex.getErrorCode());
    }
}
