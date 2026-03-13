package com.viettelpost.fms.utm_integration.command.service;

import com.viettelpost.fms.utm_integration.command.domain.CommandStatus;
import com.viettelpost.fms.utm_integration.command.domain.UtmCommandEntity;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandMessage;
import com.viettelpost.fms.utm_integration.command.repository.UtmCommandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtmCommandServiceImplTest {

    @Mock
    private UtmCommandRepository utmCommandRepository;

    @Mock
    private UtmCommandAckService utmCommandAckService;

    @InjectMocks
    private UtmCommandServiceImpl utmCommandService;

    @Test
    void receiveShouldPersistCommandAndPrepareAckWhenCommandIsNew() {
        Date receivedAt = new Date();
        when(utmCommandRepository.findByCommandId("cmd-1")).thenReturn(Optional.empty());
        when(utmCommandRepository.save(any(UtmCommandEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = utmCommandService.receive(UtmCommandMessage.builder()
                .commandId("cmd-1")
                .missionId("mission-1")
                .commandType("RETURN_HOME")
                .priority(1)
                .payload("{\"altitude\":120}")
                .receivedAt(receivedAt)
                .build());

        assertEquals(CommandStatus.RECEIVED, result.getStatus());
        assertEquals("cmd-1", result.getCommandId());
        assertEquals(receivedAt, result.getReceivedAt());
        verify(utmCommandRepository).save(any(UtmCommandEntity.class));
        verify(utmCommandAckService).prepareAck(result);
    }

    @Test
    void receiveShouldReturnExistingCommandWithoutCreatingDuplicate() {
        UtmCommandEntity existing = UtmCommandEntity.builder()
                .id("id-1")
                .commandId("cmd-1")
                .missionId("mission-1")
                .commandType("RETURN_HOME")
                .priority(1)
                .payload("payload")
                .status(CommandStatus.RECEIVED)
                .receivedAt(new Date())
                .build();
        when(utmCommandRepository.findByCommandId("cmd-1")).thenReturn(Optional.of(existing));

        var result = utmCommandService.receive(UtmCommandMessage.builder()
                .commandId("cmd-1")
                .commandType("RETURN_HOME")
                .build());

        assertEquals("id-1", result.getId());
        assertEquals(CommandStatus.RECEIVED, result.getStatus());
        assertNotNull(result.getReceivedAt());
        verify(utmCommandRepository, never()).save(any(UtmCommandEntity.class));
        verify(utmCommandAckService, never()).prepareAck(any());
    }
}
