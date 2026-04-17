package com.viettelpost.fms.utm_integration.registry.client;

import java.util.List;

public interface UtmPilotRegistryClient {

    PilotRegistryRecord create(String accessToken, PilotRegistryUpsertRequest request);

    PilotRegistryRecord getById(String accessToken, String utmPilotId);

    List<PilotRegistryRecord> search(String accessToken, PilotRegistrySearchRequest request);

    PilotRegistryRecord update(String accessToken, String utmPilotId, PilotRegistryUpsertRequest request);
}