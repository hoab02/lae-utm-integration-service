package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistrySubmissionResult;
import com.viettelpost.fms.utm_integration.registry.client.UtmDroneRegistryClient;
import com.viettelpost.fms.utm_integration.registry.domain.DroneRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.repository.DroneRegistrationRepository;
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
class DroneRegistrationServiceImplTest {

    @Mock
    private DroneRegistrationRepository droneRegistrationRepository;

    @Mock
    private UtmDroneRegistryClient utmDroneRegistryClient;

    @Mock
    private UtmSessionService utmSessionService;

    @InjectMocks
    private DroneRegistrationServiceImpl droneRegistrationService;

    @Test
    void submitShouldPersistSubmittedDroneRegistration() throws InternalException {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(droneRegistrationRepository.findByDroneId("drone-1")).thenReturn(Optional.empty());
        when(utmDroneRegistryClient.submit(any())).thenReturn(new DroneRegistrySubmissionResult("utm-drone-1"));
        when(droneRegistrationRepository.save(any(DroneRegistrationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = droneRegistrationService.submit(DroneRegistrationSubmitRequest.builder().droneId("drone-1").build());

        assertEquals("drone-1", result.getDroneId());
        assertEquals("utm-drone-1", result.getUtmDroneId());
        assertEquals(RegistrationStatus.SUBMITTED, result.getStatus());
        verify(droneRegistrationRepository).save(any(DroneRegistrationEntity.class));
    }

    @Test
    void submitShouldRejectWhenDroneRegistrationAlreadyExistsInApprovedState() {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(droneRegistrationRepository.findByDroneId("drone-1")).thenReturn(Optional.of(DroneRegistrationEntity.builder()
                .droneId("drone-1")
                .status(RegistrationStatus.APPROVED)
                .build()));

        InternalException ex = assertThrows(InternalException.class,
                () -> droneRegistrationService.submit(DroneRegistrationSubmitRequest.builder().droneId("drone-1").build()));

        assertEquals(ErrorCode.ERROR_REGISTRATION_ALREADY_SUBMITTED.name(), ex.getErrorCode());
    }

    @Test
    void getByDroneIdShouldRejectWhenRegistrationIsMissing() {
        when(droneRegistrationRepository.findByDroneId("drone-404")).thenReturn(Optional.empty());

        InternalException ex = assertThrows(InternalException.class, () -> droneRegistrationService.getByDroneId("drone-404"));

        assertEquals(ErrorCode.ERROR_DRONE_REGISTRATION_NOT_FOUND.name(), ex.getErrorCode());
    }
}
