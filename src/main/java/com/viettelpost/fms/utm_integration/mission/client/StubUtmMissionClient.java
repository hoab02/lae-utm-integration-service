package com.viettelpost.fms.utm_integration.mission.client;

import com.viettelpost.fms.utm_integration.mission.dto.UtmMissionEventRequest;
import org.springframework.stereotype.Component;

@Component
public class StubUtmMissionClient implements UtmMissionClient {

    @Override
    public void reportAirborne(UtmMissionEventRequest request) {
        // Stub outbound adapter for phase 4 wiring.
    }

    @Override
    public void reportLanding(UtmMissionEventRequest request) {
        // Stub outbound adapter for phase 4 wiring.
    }

    @Override
    public void reportCompletion(UtmMissionEventRequest request) {
        // Stub outbound adapter for phase 4 wiring.
    }

    @Override
    public void reportEmergency(UtmMissionEventRequest request) {
        // Stub outbound adapter for phase 4 wiring.
    }
}