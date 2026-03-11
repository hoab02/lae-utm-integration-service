package com.viettelpost.fms.utm_integration.session.client;

import com.viettelpost.fms.utm_integration.session.dto.UtmSessionClientConnectResult;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionClientDisconnectRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class StubUtmSessionClient implements UtmSessionClient {

    @Override
    public UtmSessionClientConnectResult connect() {
        Date connectedAt = new Date();
        Date expiresAt = new Date(connectedAt.getTime() + 30L * 60L * 1000L);
        return UtmSessionClientConnectResult.builder()
                .sessionId(UUID.randomUUID().toString())
                .token("stub-token-" + UUID.randomUUID())
                .connectedAt(connectedAt)
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    public void disconnect(UtmSessionClientDisconnectRequest request) {
        // Stub outbound adapter for phase 2 wiring.
    }
}
