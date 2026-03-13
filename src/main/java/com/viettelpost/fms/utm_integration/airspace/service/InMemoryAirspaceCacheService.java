package com.viettelpost.fms.utm_integration.airspace.service;

import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InMemoryAirspaceCacheService implements AirspaceCacheService {

    private final Map<AirspaceUpdateType, AirspaceUpdateStatusDto> latestByType = new EnumMap<>(AirspaceUpdateType.class);

    @Override
    public synchronized void put(AirspaceUpdateStatusDto update) {
        latestByType.put(update.getType(), update);
    }

    @Override
    public synchronized Optional<AirspaceUpdateStatusDto> getLatest(AirspaceUpdateType type) {
        return Optional.ofNullable(latestByType.get(type));
    }
}
