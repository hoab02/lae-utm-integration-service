package com.viettelpost.fms.utm_integration;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.session.client.UtmSessionClient;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionClientConnectResult;
import com.viettelpost.fms.utm_integration.session.repository.UtmSessionRepository;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationTests {

    @Mock
    private UtmSessionRepository utmSessionRepository;

    @Mock
    private UtmSessionClient utmSessionClient;

    @InjectMocks
    private UtmSessionServiceImpl utmSessionService;

    @Test
    void connectShouldRejectWhenSessionAlreadyConnected() {
        UtmSessionEntity session = UtmSessionEntity.builder()
                .status(SessionStatus.CONNECTED)
                .build();
        when(utmSessionRepository.findTopByOrderByCreatedDateDesc()).thenReturn(Optional.of(session));

        InternalException ex = assertThrows(InternalException.class, () -> utmSessionService.connect());

        assertEquals(ErrorCode.ERROR_SESSION_ALREADY_CONNECTED.name(), ex.getErrorCode());
        verify(utmSessionClient, never()).connect();
    }

    @Test
    void disconnectShouldRejectWhenSessionIsNotConnected() {
        when(utmSessionRepository.findTopByOrderByCreatedDateDesc()).thenReturn(Optional.empty());

        InternalException ex = assertThrows(InternalException.class, () -> utmSessionService.disconnect());

        assertEquals(ErrorCode.ERROR_SESSION_NOT_CONNECTED.name(), ex.getErrorCode());
        verify(utmSessionClient, never()).disconnect(any());
    }

    @Test
    void connectShouldPersistConnectedSession() throws InternalException {
        Date connectedAt = new Date();
        UtmSessionClientConnectResult clientResult = UtmSessionClientConnectResult.builder()
                .sessionId("session-1")
                .token("token-1")
                .connectedAt(connectedAt)
                .expiresAt(new Date(connectedAt.getTime() + 1000L))
                .build();
        when(utmSessionRepository.findTopByOrderByCreatedDateDesc()).thenReturn(Optional.empty());
        when(utmSessionClient.connect()).thenReturn(clientResult);
        when(utmSessionRepository.save(any(UtmSessionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = utmSessionService.connect();

        assertEquals(SessionStatus.CONNECTED, result.getStatus());
        assertEquals("session-1", result.getSessionId());
        verify(utmSessionRepository).save(any(UtmSessionEntity.class));
    }
}
