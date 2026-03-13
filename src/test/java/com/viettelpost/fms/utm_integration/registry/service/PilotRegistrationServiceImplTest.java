package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.client.PilotRegistrySubmissionResult;
import com.viettelpost.fms.utm_integration.registry.client.UtmPilotRegistryClient;
import com.viettelpost.fms.utm_integration.registry.domain.PilotRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.repository.PilotRegistrationRepository;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PilotRegistrationServiceImplTest {

    @Mock
    private PilotRegistrationRepository pilotRegistrationRepository;

    @Mock
    private UtmPilotRegistryClient utmPilotRegistryClient;

    @Mock
    private UtmSessionService utmSessionService;

    @InjectMocks
    private PilotRegistrationServiceImpl pilotRegistrationService;

    @Test
    void submitShouldPersistSubmittedPilotRegistration() throws InternalException {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(pilotRegistrationRepository.findByPilotId("pilot-1")).thenReturn(Optional.empty());
        when(utmPilotRegistryClient.submit(any())).thenReturn(new PilotRegistrySubmissionResult("utm-pilot-1"));
        when(pilotRegistrationRepository.save(any(PilotRegistrationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = pilotRegistrationService.submit(PilotRegistrationSubmitRequest.builder().pilotId("pilot-1").build());

        assertEquals("pilot-1", result.getPilotId());
        assertEquals("utm-pilot-1", result.getUtmPilotId());
        assertEquals(RegistrationStatus.SUBMITTED, result.getStatus());
        verify(pilotRegistrationRepository).save(any(PilotRegistrationEntity.class));
    }

    @Test
    void submitShouldRejectWhenPilotRegistrationAlreadyExistsInSubmittedState() {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(pilotRegistrationRepository.findByPilotId("pilot-1")).thenReturn(Optional.of(PilotRegistrationEntity.builder()
                .pilotId("pilot-1")
                .status(RegistrationStatus.SUBMITTED)
                .build()));

        InternalException ex = assertThrows(InternalException.class,
                () -> pilotRegistrationService.submit(PilotRegistrationSubmitRequest.builder().pilotId("pilot-1").build()));

        assertEquals(ErrorCode.ERROR_REGISTRATION_ALREADY_SUBMITTED.name(), ex.getErrorCode());
    }

    @Test
    void getByPilotIdShouldRejectWhenRegistrationIsMissing() {
        when(pilotRegistrationRepository.findByPilotId("pilot-404")).thenReturn(Optional.empty());

        InternalException ex = assertThrows(InternalException.class, () -> pilotRegistrationService.getByPilotId("pilot-404"));

        assertEquals(ErrorCode.ERROR_PILOT_REGISTRATION_NOT_FOUND.name(), ex.getErrorCode());
    }
}
