package com.viettelpost.fms.utm_integration.registry.client;

import java.util.List;

public interface UtmDroneRegistryClient {

    DroneRegistryRecord create(String accessToken, DroneRegistryUpsertRequest request);

    DroneRegistryRecord getById(String accessToken, String utmDroneId);

    List<DroneRegistryRecord> search(String accessToken, DroneRegistrySearchRequest request);

    DroneRegistryRecord update(String accessToken, String utmDroneId, DroneRegistryUpsertRequest request);
}