package com.viettelpost.fms.utm_integration.command.subscriber;

import com.viettelpost.fms.utm_integration.command.dto.UtmCommandMessage;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;
import com.viettelpost.fms.utm_integration.command.service.UtmCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtmCommandMqttSubscriberTest {

    @Mock
    private UtmCommandService utmCommandService;

    @InjectMocks
    private UtmCommandMqttSubscriber utmCommandMqttSubscriber;

    @Test
    void receiveShouldDelegateToCommandService() throws Exception {
        UtmCommandMessage message = UtmCommandMessage.builder()
                .commandId("cmd-1")
                .commandType("RETURN_HOME")
                .build();
        UtmCommandStatusDto response = UtmCommandStatusDto.builder()
                .commandId("cmd-1")
                .build();
        when(utmCommandService.receive(message)).thenReturn(response);

        var result = utmCommandMqttSubscriber.receive(message);

        assertEquals("cmd-1", result.getCommandId());
        verify(utmCommandService).receive(message);
    }
}
