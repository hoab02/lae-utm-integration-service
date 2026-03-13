package com.viettelpost.fms.utm_integration.airspace.service;

import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;

import java.util.Optional;

public interface AirspaceCacheService {

    void put(AirspaceUpdateStatusDto update);

    Optional<AirspaceUpdateStatusDto> getLatest(AirspaceUpdateType type);
}
