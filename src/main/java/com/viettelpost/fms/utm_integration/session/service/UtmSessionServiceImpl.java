package com.viettelpost.fms.utm_integration.session.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.session.client.UtmSessionClient;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionClientConnectResult;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionClientDisconnectRequest;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionStatusDto;
import com.viettelpost.fms.utm_integration.session.repository.UtmSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UtmSessionServiceImpl implements UtmSessionService {

    private final UtmSessionRepository utmSessionRepository;
    private final UtmSessionClient utmSessionClient;

    @Override
    @Transactional
    public UtmSessionStatusDto connect() throws InternalException {
        UtmSessionEntity session = getOrCreateSession();
        validateConnectTransition(session);

        UtmSessionClientConnectResult connectResult = utmSessionClient.connect();
        session.setSessionId(connectResult.sessionId());
        session.setToken(connectResult.token());
        session.setStatus(SessionStatus.CONNECTED);
        session.setConnectedAt(connectResult.connectedAt());
        session.setLastHeartbeatAt(connectResult.connectedAt());
        session.setExpiresAt(connectResult.expiresAt());
        session.setFailureReason(null);

        return toDto(utmSessionRepository.save(session));
    }

    @Override
    @Transactional
    public UtmSessionStatusDto disconnect() throws InternalException {
        UtmSessionEntity session = getOrCreateSession();
        validateDisconnectTransition(session);
        if (session.getSessionId() != null || session.getToken() != null) {
            utmSessionClient.disconnect(new UtmSessionClientDisconnectRequest(session.getSessionId(), session.getToken()));
        }

        session.setStatus(SessionStatus.DISCONNECTED);
        session.setToken(null);
        session.setLastHeartbeatAt(null);
        session.setExpiresAt(null);
        session.setFailureReason(null);
        if (session.getConnectedAt() == null) {
            session.setConnectedAt(new Date());
        }

        return toDto(utmSessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public UtmSessionStatusDto getCurrentStatus() {
        return utmSessionRepository.findTopByOrderByCreatedDateDesc()
                .map(this::toDto)
                .orElseGet(() -> UtmSessionStatusDto.builder()
                        .status(SessionStatus.DISCONNECTED)
                        .build());
    }

    private UtmSessionEntity getOrCreateSession() {
        return utmSessionRepository.findTopByOrderByCreatedDateDesc()
                .orElseGet(() -> UtmSessionEntity.builder()
                        .status(SessionStatus.DISCONNECTED)
                        .build());
    }

    private void validateConnectTransition(UtmSessionEntity session) throws InternalException {
        if (SessionStatus.CONNECTED.equals(session.getStatus())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_ALREADY_CONNECTED);
        }
    }

    private void validateDisconnectTransition(UtmSessionEntity session) throws InternalException {
        if (!SessionStatus.CONNECTED.equals(session.getStatus())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
    }

    private UtmSessionStatusDto toDto(UtmSessionEntity session) {
        return UtmSessionStatusDto.builder()
                .id(session.getId())
                .sessionId(session.getSessionId())
                .status(session.getStatus())
                .connectedAt(session.getConnectedAt())
                .lastHeartbeatAt(session.getLastHeartbeatAt())
                .expiresAt(session.getExpiresAt())
                .failureReason(session.getFailureReason())
                .build();
    }
}
