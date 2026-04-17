package com.viettelpost.fms.utm_integration.session.worker;

import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import com.viettelpost.fms.utm_integration.session.service.UtmTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UtmTokenRefreshWorker {

    private final UtmTokenManager utmTokenManager;

    @Scheduled(fixedDelayString = "${integration.utm.worker.refresh-interval-ms:60000}")
    public void refreshIfNeeded() {
        try {
            if (SessionStatus.DISCONNECTED.equals(utmTokenManager.getCurrentSessionContext().status())) {
                return;
            }
            UtmSessionEntity refreshed = utmTokenManager.refreshIfNeeded();
            log.info("utm_token_refresh_worker_success dcsId={} status={}",
                    refreshed.getDcsId(), refreshed.getStatus());
        } catch (RuntimeException ex) {
            log.error("utm_token_refresh_worker_failed", ex);
        }
    }
}
