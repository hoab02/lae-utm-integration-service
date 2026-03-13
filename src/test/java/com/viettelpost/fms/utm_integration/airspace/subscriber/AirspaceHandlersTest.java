package com.viettelpost.fms.utm_integration.airspace.subscriber;

import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateMessage;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;
import com.viettelpost.fms.utm_integration.airspace.service.AirspaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirspaceHandlersTest {

    @Mock
    private AirspaceService airspaceService;

    @InjectMocks
    private NfzUpdateHandler nfzUpdateHandler;

    @InjectMocks
    private CorridorUpdateHandler corridorUpdateHandler;

    @Test
    void nfzHandlerShouldDelegateWithNfzType() throws Exception {
        var response = AirspaceUpdateStatusDto.builder().updateId("update-1").type(AirspaceUpdateType.NFZ).build();
        when(airspaceService.receive(org.mockito.ArgumentMatchers.any(AirspaceUpdateMessage.class))).thenReturn(response);

        nfzUpdateHandler.receive(AirspaceUpdateMessage.builder()
                .updateId("update-1")
                .type(AirspaceUpdateType.CORRIDOR)
                .payload("payload")
                .build());

        ArgumentCaptor<AirspaceUpdateMessage> captor = ArgumentCaptor.forClass(AirspaceUpdateMessage.class);
        verify(airspaceService).receive(captor.capture());
        assertEquals(AirspaceUpdateType.NFZ, captor.getValue().getType());
    }

    @Test
    void corridorHandlerShouldDelegateWithCorridorType() throws Exception {
        var response = AirspaceUpdateStatusDto.builder().updateId("update-2").type(AirspaceUpdateType.CORRIDOR).build();
        when(airspaceService.receive(org.mockito.ArgumentMatchers.any(AirspaceUpdateMessage.class))).thenReturn(response);

        corridorUpdateHandler.receive(AirspaceUpdateMessage.builder()
                .updateId("update-2")
                .type(AirspaceUpdateType.NFZ)
                .payload("payload")
                .build());

        ArgumentCaptor<AirspaceUpdateMessage> captor = ArgumentCaptor.forClass(AirspaceUpdateMessage.class);
        verify(airspaceService).receive(captor.capture());
        assertEquals(AirspaceUpdateType.CORRIDOR, captor.getValue().getType());
    }
}
