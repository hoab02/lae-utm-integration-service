package com.viettelpost.fms.utm_integration.airspace.service;

import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateEntity;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateStatus;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateMessage;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;
import com.viettelpost.fms.utm_integration.airspace.repository.AirspaceUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirspaceServiceImpl implements AirspaceService {

    private final AirspaceUpdateRepository airspaceUpdateRepository;
    private final AirspaceCacheService airspaceCacheService;

    @Override
    @Transactional
    public AirspaceUpdateStatusDto receive(AirspaceUpdateMessage message) {
        return airspaceUpdateRepository.findByUpdateId(message.getUpdateId())
                .map(this::toDto)
                .orElseGet(() -> storeNew(message));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AirspaceUpdateStatusDto> getLatest(AirspaceUpdateType type) {
        Optional<AirspaceUpdateStatusDto> cached = airspaceCacheService.getLatest(type);
        if (cached.isPresent()) {
            return cached;
        }

        Optional<AirspaceUpdateStatusDto> latest = airspaceUpdateRepository
                .findTopByTypeAndStatusOrderByEffectiveFromDescReceivedAtDesc(type, AirspaceUpdateStatus.RECEIVED)
                .map(this::toDto);
        latest.ifPresent(airspaceCacheService::put);
        return latest;
    }

    private AirspaceUpdateStatusDto storeNew(AirspaceUpdateMessage message) {
        markCurrentLatestAsSuperseded(message.getType());

        AirspaceUpdateEntity update = AirspaceUpdateEntity.builder()
                .updateId(message.getUpdateId())
                .type(message.getType())
                .version(message.getVersion())
                .payload(message.getPayload())
                .effectiveFrom(message.getEffectiveFrom())
                .receivedAt(message.getReceivedAt() != null ? message.getReceivedAt() : new Date())
                .source(message.getSource())
                .status(AirspaceUpdateStatus.RECEIVED)
                .build();

        AirspaceUpdateStatusDto status = toDto(airspaceUpdateRepository.save(update));
        airspaceCacheService.put(status);
        return status;
    }

    private void markCurrentLatestAsSuperseded(AirspaceUpdateType type) {
        airspaceUpdateRepository.findTopByTypeAndStatusOrderByEffectiveFromDescReceivedAtDesc(type, AirspaceUpdateStatus.RECEIVED)
                .ifPresent(existing -> {
                    existing.setStatus(AirspaceUpdateStatus.SUPERSEDED);
                    airspaceUpdateRepository.save(existing);
                });
    }

    private AirspaceUpdateStatusDto toDto(AirspaceUpdateEntity update) {
        return AirspaceUpdateStatusDto.builder()
                .id(update.getId())
                .updateId(update.getUpdateId())
                .type(update.getType())
                .version(update.getVersion())
                .payload(update.getPayload())
                .effectiveFrom(update.getEffectiveFrom())
                .receivedAt(update.getReceivedAt())
                .source(update.getSource())
                .status(update.getStatus())
                .build();
    }
}
