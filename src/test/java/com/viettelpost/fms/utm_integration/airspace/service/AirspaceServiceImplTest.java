package com.viettelpost.fms.utm_integration.airspace.service;

import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateEntity;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateStatus;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateMessage;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;
import com.viettelpost.fms.utm_integration.airspace.repository.AirspaceUpdateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirspaceServiceImplTest {

    @Mock
    private AirspaceUpdateRepository airspaceUpdateRepository;

    @Mock
    private AirspaceCacheService airspaceCacheService;

    @InjectMocks
    private AirspaceServiceImpl airspaceService;

    @Test
    void receiveShouldPersistNewUpdateAndCacheIt() {
        Date effectiveFrom = new Date();
        when(airspaceUpdateRepository.findByUpdateId("update-1")).thenReturn(Optional.empty());
        when(airspaceUpdateRepository.findTopByTypeAndStatusOrderByEffectiveFromDescReceivedAtDesc(AirspaceUpdateType.NFZ, AirspaceUpdateStatus.RECEIVED))
                .thenReturn(Optional.empty());
        when(airspaceUpdateRepository.save(any(AirspaceUpdateEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = airspaceService.receive(AirspaceUpdateMessage.builder()
                .updateId("update-1")
                .type(AirspaceUpdateType.NFZ)
                .version("v1")
                .payload("{\"zones\":[]}")
                .effectiveFrom(effectiveFrom)
                .source("utm")
                .build());

        assertEquals("update-1", result.getUpdateId());
        assertEquals(AirspaceUpdateType.NFZ, result.getType());
        assertEquals(AirspaceUpdateStatus.RECEIVED, result.getStatus());
        assertEquals(effectiveFrom, result.getEffectiveFrom());
        verify(airspaceCacheService).put(result);
    }

    @Test
    void receiveShouldReturnExistingUpdateWhenUpdateIdAlreadyExists() {
        AirspaceUpdateEntity existing = AirspaceUpdateEntity.builder()
                .id("id-1")
                .updateId("update-1")
                .type(AirspaceUpdateType.CORRIDOR)
                .version("v1")
                .payload("payload")
                .receivedAt(new Date())
                .status(AirspaceUpdateStatus.RECEIVED)
                .build();
        when(airspaceUpdateRepository.findByUpdateId("update-1")).thenReturn(Optional.of(existing));

        var result = airspaceService.receive(AirspaceUpdateMessage.builder()
                .updateId("update-1")
                .type(AirspaceUpdateType.CORRIDOR)
                .payload("payload")
                .build());

        assertEquals("id-1", result.getId());
        assertEquals(AirspaceUpdateType.CORRIDOR, result.getType());
        verify(airspaceUpdateRepository, never()).save(any(AirspaceUpdateEntity.class));
        verify(airspaceCacheService, never()).put(any(AirspaceUpdateStatusDto.class));
    }

    @Test
    void getLatestShouldFallbackToRepositoryAndPopulateCache() {
        AirspaceUpdateEntity latest = AirspaceUpdateEntity.builder()
                .id("id-1")
                .updateId("update-2")
                .type(AirspaceUpdateType.NFZ)
                .version("v2")
                .payload("payload")
                .receivedAt(new Date())
                .status(AirspaceUpdateStatus.RECEIVED)
                .build();
        when(airspaceCacheService.getLatest(AirspaceUpdateType.NFZ)).thenReturn(Optional.empty());
        when(airspaceUpdateRepository.findTopByTypeAndStatusOrderByEffectiveFromDescReceivedAtDesc(AirspaceUpdateType.NFZ, AirspaceUpdateStatus.RECEIVED))
                .thenReturn(Optional.of(latest));

        var result = airspaceService.getLatest(AirspaceUpdateType.NFZ);

        assertTrue(result.isPresent());
        assertEquals("update-2", result.get().getUpdateId());
        verify(airspaceCacheService).put(any(AirspaceUpdateStatusDto.class));
    }
}
