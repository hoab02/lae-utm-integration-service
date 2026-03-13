package com.viettelpost.fms.utm_integration.mission.client;

import com.viettelpost.fms.utm_integration.mission.dto.UtmMissionEventRequest;

public interface UtmMissionClient {

    void reportAirborne(UtmMissionEventRequest request);

    void reportLanding(UtmMissionEventRequest request);

    void reportCompletion(UtmMissionEventRequest request);

    void reportEmergency(UtmMissionEventRequest request);
}