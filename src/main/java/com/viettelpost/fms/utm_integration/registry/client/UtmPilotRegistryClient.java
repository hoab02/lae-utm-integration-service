package com.viettelpost.fms.utm_integration.registry.client;

import com.viettelpost.fms.utm_integration.exception.InternalException;

import java.util.List;

public interface UtmPilotRegistryClient {

    PilotRegistryRecord create(String accessToken, PilotRegistryUpsertRequest request) throws InternalException;

    PilotRegistryRecord getById(String accessToken, String utmPilotId) throws InternalException;

    List<PilotRegistryRecord> search(String accessToken, PilotRegistrySearchRequest request) throws InternalException;

    PilotRegistryRecord update(String accessToken, String utmPilotId, PilotRegistryUpsertRequest request) throws InternalException;
}