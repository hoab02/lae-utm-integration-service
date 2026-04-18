package com.viettelpost.fms.utm_integration.registry.client;

import com.viettelpost.fms.utm_integration.exception.InternalException;

import java.util.List;

public interface UtmDroneRegistryClient {

    DroneRegistryRecord create(String accessToken, DroneRegistryUpsertRequest request) throws InternalException;

    DroneRegistryRecord getById(String accessToken, String utmDroneId) throws InternalException;

    List<DroneRegistryRecord> search(String accessToken, DroneRegistrySearchRequest request) throws InternalException;

    DroneRegistryRecord update(String accessToken, String utmDroneId, DroneRegistryUpsertRequest request) throws InternalException;
}