package com.viettelpost.fms.utm_integration.session.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionStatusDto;
import com.viettelpost.fms.utm_integration.session.repository.UtmSessionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UtmSessionServiceImpl implements UtmSessionService, UtmSessionContextProvider {

    private final UtmSessionRepository utmSessionRepository;
    private final UtmTokenManager utmTokenManager;
    private final MeterRegistry meterRegistry;

    @Override
    public UtmSessionStatusDto connect() throws InternalException {
        log.info("utm_session_connect_start");
        try {
            UtmSessionEntity session = getOrCreateSession();
            validateConnectTransition(session);
            UtmSessionStatusDto status = toStatusDto(utmTokenManager.connect());
            log.info("utm_session_connect_success dcsId={} status={}", status.dcsId(), status.status());
            return status;
        } catch (InternalException | RuntimeException ex) {
            meterRegistry.counter("utm.session.failure.total").increment();
            log.error("utm_session_connect_failure errorType={} errorCode={}",
                    ex.getClass().getSimpleName(), ex instanceof InternalException ie ? ie.getErrorCode() : "UNEXPECTED_ERROR", ex);
            throw ex;
        }
    }

    @Override
    public UtmSessionStatusDto refreshIfNeeded() {
        log.info("utm_session_refresh_start");
        try {
            UtmSessionStatusDto status = toStatusDto(utmTokenManager.refreshIfNeeded());
            log.info("utm_session_refresh_success dcsId={} status={}", status.dcsId(), status.status());
            return status;
        } catch (RuntimeException ex) {
            meterRegistry.counter("utm.session.failure.total").increment();
            log.error("utm_session_refresh_failure errorType={}", ex.getClass().getSimpleName(), ex);
            throw ex;
        }
    }

    @Override
    public UtmSessionStatusDto disconnect() throws InternalException {
        log.info("utm_session_disconnect_start");
        try {
            UtmSessionEntity session = getRequiredConnectedSession();
            validateDisconnectTransition(session);
            UtmSessionStatusDto status = toStatusDto(utmTokenManager.disconnect());
            log.info("utm_session_disconnect_success dcsId={} status={}", status.dcsId(), status.status());
            return status;
        } catch (InternalException | RuntimeException ex) {
            meterRegistry.counter("utm.session.failure.total").increment();
            log.error("utm_session_disconnect_failure errorType={} errorCode={}",
                    ex.getClass().getSimpleName(), ex instanceof InternalException ie ? ie.getErrorCode() : "UNEXPECTED_ERROR", ex);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UtmSessionStatusDto getCurrentStatus() {
        return utmSessionRepository.findTopByOrderByCreatedDateDesc()
                .map(this::toStatusDto)
                .orElse(UtmSessionStatusDto.builder()
                        .status(SessionStatus.DISCONNECTED)
                        .active(Boolean.FALSE)
                        .build());
    }

    @Transactional(readOnly = true)
    @Override
    public UtmSessionContextDto getCurrentSessionContext() {
        return utmTokenManager.getCurrentSessionContext();
    }

    @Transactional(readOnly = true)
    @Override
    public UtmSessionContextDto getRequiredSessionContext() throws InternalException {
        UtmSessionContextDto sessionContext = getCurrentSessionContext();
        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
        return sessionContext;
    }

    private UtmSessionEntity getOrCreateSession() {
        return utmSessionRepository.findTopByOrderByCreatedDateDesc()
                .orElse(UtmSessionEntity.builder()
                        .status(SessionStatus.DISCONNECTED)
                        .active(Boolean.FALSE)
                        .build());
    }

    private UtmSessionEntity getRequiredConnectedSession() {
        return utmSessionRepository.findTopByOrderByCreatedDateDesc()
                .filter(s -> SessionStatus.CONNECTED.equals(s.getStatus()))
                .orElseThrow(() -> new IllegalStateException("UTM session is not connected"));
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

    private UtmSessionStatusDto toStatusDto(UtmSessionEntity entity) {
        return UtmSessionStatusDto.builder()
                .id(entity.getId())
                .dcsId(entity.getDcsId())
                .status(entity.getStatus())
                .active(entity.getActive())
                .connectedAt(entity.getConnectedAt())
                .lastRefreshAt(entity.getLastRefreshAt())
                .accessTokenExpiresAt(entity.getAccessTokenExpiresAt())
                .refreshTokenExpiresAt(entity.getRefreshTokenExpiresAt())
                .disconnectedAt(entity.getDisconnectedAt())
                .failureReason(entity.getFailureReason())
                .build();
    }
}
