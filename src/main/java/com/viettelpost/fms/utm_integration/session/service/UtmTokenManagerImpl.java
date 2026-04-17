package com.viettelpost.fms.utm_integration.session.service;

import com.viettelpost.fms.utm_integration.session.client.UtmAuthClient;
import com.viettelpost.fms.utm_integration.session.config.UtmSessionProperties;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionStatusDto;
import com.viettelpost.fms.utm_integration.session.dto.request.UtmRefreshTokenRequest;
import com.viettelpost.fms.utm_integration.session.dto.request.UtmTokenRequest;
import com.viettelpost.fms.utm_integration.session.dto.response.UtmTokenResponse;
import com.viettelpost.fms.utm_integration.session.kafka.SessionStatusKafkaPublisher;
import com.viettelpost.fms.utm_integration.session.repository.UtmSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UtmTokenManagerImpl implements UtmTokenManager {

    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String REFRESH_GRANT_TYPE = "refresh_token";

    private final UtmSessionRepository utmSessionRepository;
    private final UtmAuthClient utmAuthClient;
    private final UtmSessionProperties sessionProperties;
    private final SessionStatusKafkaPublisher sessionStatusKafkaPublisher;

    @Override
    public synchronized UtmSessionEntity connect() {
        Date now = new Date();
        UtmSessionEntity session = getOrCreateSession();

        session.setStatus(SessionStatus.AUTHENTICATING);
        session.setDcsId(sessionProperties.getDcsId());
        session.setActive(Boolean.FALSE);
        session.setFailureReason(null);
        session.setDisconnectedAt(null);
        utmSessionRepository.save(session);

        try {
            UtmTokenResponse response = utmAuthClient.requestToken(new UtmTokenRequest(
                    PASSWORD_GRANT_TYPE,
                    sessionProperties.resolveUsername(),
                    sessionProperties.getPassword()
            ));

            applyTokenResponse(session, response, now);
            session.setConnectedAt(now);
            session.setLastRefreshAt(now);
            session.setStatus(SessionStatus.CONNECTED);
            session.setActive(Boolean.TRUE);
            session.setFailureReason(null);
            session.setDisconnectedAt(null);

            UtmSessionEntity saved = utmSessionRepository.save(session);
            publish(saved);
            return saved;
        } catch (RuntimeException ex) {
            markFailed(session, "Initial token request failed", ex);
            throw ex;
        }
    }

    @Override
    public synchronized UtmSessionEntity refreshIfNeeded() {
        UtmSessionEntity session = getOrCreateSession();
        if (!StringUtils.hasText(session.getAccessToken())) {
            return connect();
        }
        if (!shouldRefresh(session)) {
            return session;
        }
        if (!StringUtils.hasText(session.getRefreshToken()) || isExpired(session.getRefreshTokenExpiresAt())) {
            log.warn("utm_token_refresh_unavailable fallback=login reason=refresh_token_missing_or_expired");
            return connect();
        }

        try {
            UtmTokenResponse response = utmAuthClient.refreshToken(new UtmRefreshTokenRequest(
                    REFRESH_GRANT_TYPE,
                    session.getRefreshToken()
            ));

            Date now = new Date();
            applyTokenResponse(session, response, now);
            session.setStatus(SessionStatus.CONNECTED);
            session.setActive(Boolean.TRUE);
            session.setLastRefreshAt(now);
            session.setFailureReason(null);
            session.setDisconnectedAt(null);

            UtmSessionEntity saved = utmSessionRepository.save(session);
            publish(saved);
            return saved;
        } catch (RuntimeException ex) {
            log.warn("utm_token_refresh_failed fallback=login errorType={}", ex.getClass().getSimpleName(), ex);
            return connect();
        }
    }

    @Override
    public String getValidAccessToken() {
        UtmSessionEntity session = refreshIfNeeded();
        if (!StringUtils.hasText(session.getAccessToken())) {
            throw new IllegalStateException("UTM access token is unavailable");
        }
        return session.getAccessToken();
    }

    @Override
    @Transactional(readOnly = true)
    public UtmSessionContextDto getCurrentSessionContext() {
        return utmSessionRepository.findTopByOrderByCreatedDateDesc()
                .map(this::toContextDto)
                .orElse(UtmSessionContextDto.builder()
                        .status(SessionStatus.DISCONNECTED)
                        .active(Boolean.FALSE)
                        .build());
    }

    @Override
    public synchronized UtmSessionEntity disconnect() {
        UtmSessionEntity session = getOrCreateSession();
        session.setStatus(SessionStatus.DISCONNECTED);
        session.setActive(Boolean.FALSE);
        session.setAccessToken(null);
        session.setRefreshToken(null);
        session.setTokenType(null);
        session.setAccessTokenExpiresAt(null);
        session.setRefreshTokenExpiresAt(null);
        session.setSessionState(null);
        session.setScope(null);
        session.setFailureReason(null);
        session.setDisconnectedAt(new Date());
        UtmSessionEntity saved = utmSessionRepository.save(session);
        publish(saved);
        return saved;
    }

    private UtmSessionEntity getOrCreateSession() {
        return utmSessionRepository.findTopByOrderByCreatedDateDesc()
                .orElseGet(() -> UtmSessionEntity.builder()
                        .dcsId(sessionProperties.getDcsId())
                        .status(SessionStatus.DISCONNECTED)
                        .active(Boolean.FALSE)
                        .build());
    }

    private boolean shouldRefresh(UtmSessionEntity session) {
        if (!StringUtils.hasText(session.getAccessToken()) || session.getAccessTokenExpiresAt() == null) {
            return true;
        }
        long refreshBeforeMillis = sessionProperties.getAuth().getRefreshBeforeSeconds() * 1000;
        return session.getAccessTokenExpiresAt().getTime() - System.currentTimeMillis() <= refreshBeforeMillis;
    }

    private boolean isExpired(Date expiresAt) {
        return expiresAt != null && !expiresAt.after(new Date());
    }

    private void applyTokenResponse(UtmSessionEntity session, UtmTokenResponse response, Date now) {
        session.setDcsId(sessionProperties.getDcsId());
        session.setAccessToken(response.accessToken());
        session.setRefreshToken(response.refreshToken());
        session.setTokenType(response.tokenType());
        session.setAccessTokenExpiresAt(resolveExpiry(now, response.expiresIn()));
        session.setRefreshTokenExpiresAt(resolveExpiry(now, response.refreshExpiresIn()));
        session.setSessionState(response.sessionState());
        session.setScope(response.scope());
    }

    private Date resolveExpiry(Date base, Long expiresInSeconds) {
        if (expiresInSeconds == null) {
            return null;
        }
        return new Date(base.getTime() + expiresInSeconds * 1000);
    }

    private void markFailed(UtmSessionEntity session, String message, RuntimeException ex) {
        session.setStatus(SessionStatus.FAILED);
        session.setActive(Boolean.FALSE);
        session.setFailureReason(message + ": " + ex.getMessage());
        UtmSessionEntity saved = utmSessionRepository.save(session);
        publish(saved);
        log.error("utm_token_connect_failed errorType={} message={}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    }

    private UtmSessionContextDto toContextDto(UtmSessionEntity entity) {
        return UtmSessionContextDto.builder()
                .accessToken(entity.getAccessToken())
                .tokenType(entity.getTokenType())
                .status(entity.getStatus())
                .active(entity.getActive())
                .accessTokenExpiresAt(entity.getAccessTokenExpiresAt())
                .refreshTokenExpiresAt(entity.getRefreshTokenExpiresAt())
                .build();
    }

    private void publish(UtmSessionEntity session) {
        sessionStatusKafkaPublisher.publish(UtmSessionStatusDto.builder()
                .id(session.getId())
                .dcsId(session.getDcsId())
                .status(session.getStatus())
                .active(session.getActive())
                .connectedAt(session.getConnectedAt())
                .lastRefreshAt(session.getLastRefreshAt())
                .accessTokenExpiresAt(session.getAccessTokenExpiresAt())
                .refreshTokenExpiresAt(session.getRefreshTokenExpiresAt())
                .disconnectedAt(session.getDisconnectedAt())
                .failureReason(session.getFailureReason())
                .build());
    }
}
