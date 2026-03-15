package com.viettelpost.fms.utm_integration.approval.service;

import com.viettelpost.fms.utm_integration.approval.client.UtmApprovalClient;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import com.viettelpost.fms.utm_integration.approval.domain.FlightApprovalEntity;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalSubmitRequest;
import com.viettelpost.fms.utm_integration.approval.dto.UtmApprovalSubmissionResult;
import com.viettelpost.fms.utm_integration.approval.repository.FlightApprovalRepository;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.service.DroneRegistrationService;
import com.viettelpost.fms.utm_integration.registry.service.PilotRegistrationService;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightApprovalServiceImplTest {

    @Mock
    private FlightApprovalRepository flightApprovalRepository;

    @Mock
    private UtmApprovalClient utmApprovalClient;

    @Mock
    private UtmSessionService utmSessionService;

    @Mock
    private PilotRegistrationService pilotRegistrationService;

    @Mock
    private DroneRegistrationService droneRegistrationService;

    @InjectMocks
    private FlightApprovalServiceImpl flightApprovalService;

    @Test
    void submitThenMarkApprovedShouldAdvanceApprovalToApproved() throws Exception {
        Date requestedAt = new Date();
        AtomicReference<FlightApprovalEntity> storedApproval = new AtomicReference<>();

        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(pilotRegistrationService.getByPilotId("pilot-1")).thenReturn(PilotRegistrationStatusDto.builder()
                .pilotId("pilot-1")
                .status(RegistrationStatus.APPROVED)
                .build());
        when(droneRegistrationService.getByDroneId("drone-1")).thenReturn(DroneRegistrationStatusDto.builder()
                .droneId("drone-1")
                .status(RegistrationStatus.APPROVED)
                .build());
        when(utmApprovalClient.submit(any())).thenReturn(UtmApprovalSubmissionResult.builder()
                .utmRequestId("utm-request-1")
                .requestedAt(requestedAt)
                .build());
        when(flightApprovalRepository.findByPlanId("plan-1")).thenAnswer(invocation -> Optional.ofNullable(storedApproval.get()));
        when(flightApprovalRepository.save(any(FlightApprovalEntity.class))).thenAnswer(invocation -> {
            FlightApprovalEntity approval = invocation.getArgument(0);
            storedApproval.set(approval);
            return approval;
        });

        var submitted = flightApprovalService.submit(FlightApprovalSubmitRequest.builder()
                .planId("plan-1")
                .missionId("mission-1")
                .droneId("drone-1")
                .pilotId("pilot-1")
                .build());
        var approved = flightApprovalService.markApproved("plan-1");

        assertEquals(ApprovalStatus.SUBMITTED, submitted.getStatus());
        assertEquals("utm-request-1", submitted.getUtmRequestId());
        assertEquals(requestedAt, submitted.getRequestedAt());
        assertEquals(ApprovalStatus.APPROVED, approved.getStatus());
        assertNotNull(approved.getApprovedAt());
        assertEquals("mission-1", approved.getMissionId());
        assertEquals("drone-1", approved.getDroneId());
    }

    @Test
    void markApprovedShouldPersistApprovedStatusWhenCurrentStatusIsSubmitted() throws Exception {
        FlightApprovalEntity approval = FlightApprovalEntity.builder()
                .planId("plan-1")
                .status(ApprovalStatus.SUBMITTED)
                .rejectedAt(new Date())
                .rejectReason("old-reason")
                .build();
        when(flightApprovalRepository.findByPlanId("plan-1")).thenReturn(Optional.of(approval));
        when(flightApprovalRepository.save(any(FlightApprovalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = flightApprovalService.markApproved("plan-1");

        assertEquals(ApprovalStatus.APPROVED, result.getStatus());
        assertNotNull(result.getApprovedAt());
        assertNull(result.getRejectedAt());
        assertNull(result.getRejectReason());
        verify(flightApprovalRepository).save(approval);
    }

    @Test
    void markApprovedShouldRejectWhenApprovalIsMissing() {
        when(flightApprovalRepository.findByPlanId("plan-404")).thenReturn(Optional.empty());

        InternalException ex = assertThrows(InternalException.class, () -> flightApprovalService.markApproved("plan-404"));

        assertEquals(ErrorCode.ERROR_APPROVAL_NOT_FOUND.name(), ex.getErrorCode());
    }

    @Test
    void markApprovedShouldRejectWhenApprovalIsNotSubmitted() {
        FlightApprovalEntity approval = FlightApprovalEntity.builder()
                .planId("plan-1")
                .status(ApprovalStatus.DRAFT)
                .build();
        when(flightApprovalRepository.findByPlanId("plan-1")).thenReturn(Optional.of(approval));

        InternalException ex = assertThrows(InternalException.class, () -> flightApprovalService.markApproved("plan-1"));

        assertEquals(ErrorCode.ERROR_REQUEST_INVALID.name(), ex.getErrorCode());
    }

    @Test
    void submitShouldRejectWhenPilotRegistrationIsMissing() throws Exception {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(flightApprovalRepository.findByPlanId("plan-1")).thenReturn(Optional.empty());
        when(pilotRegistrationService.getByPilotId("pilot-1"))
                .thenThrow(new InternalException(ErrorCode.ERROR_PILOT_REGISTRATION_NOT_FOUND));

        InternalException ex = assertThrows(InternalException.class, () -> flightApprovalService.submit(FlightApprovalSubmitRequest.builder()
                .planId("plan-1")
                .missionId("mission-1")
                .droneId("drone-1")
                .pilotId("pilot-1")
                .build()));

        assertEquals(ErrorCode.ERROR_REQUEST_INVALID.name(), ex.getErrorCode());
    }

    @Test
    void submitShouldRejectWhenPilotRegistrationIsNotApproved() throws Exception {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(flightApprovalRepository.findByPlanId("plan-1")).thenReturn(Optional.empty());
        when(pilotRegistrationService.getByPilotId("pilot-1")).thenReturn(PilotRegistrationStatusDto.builder()
                .pilotId("pilot-1")
                .status(RegistrationStatus.SUBMITTED)
                .build());

        InternalException ex = assertThrows(InternalException.class, () -> flightApprovalService.submit(FlightApprovalSubmitRequest.builder()
                .planId("plan-1")
                .missionId("mission-1")
                .droneId("drone-1")
                .pilotId("pilot-1")
                .build()));

        assertEquals(ErrorCode.ERROR_REQUEST_INVALID.name(), ex.getErrorCode());
    }

    @Test
    void submitShouldRejectWhenDroneRegistrationIsMissing() throws Exception {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(flightApprovalRepository.findByPlanId("plan-1")).thenReturn(Optional.empty());
        when(pilotRegistrationService.getByPilotId("pilot-1")).thenReturn(PilotRegistrationStatusDto.builder()
                .pilotId("pilot-1")
                .status(RegistrationStatus.APPROVED)
                .build());
        when(droneRegistrationService.getByDroneId("drone-1"))
                .thenThrow(new InternalException(ErrorCode.ERROR_DRONE_REGISTRATION_NOT_FOUND));

        InternalException ex = assertThrows(InternalException.class, () -> flightApprovalService.submit(FlightApprovalSubmitRequest.builder()
                .planId("plan-1")
                .missionId("mission-1")
                .droneId("drone-1")
                .pilotId("pilot-1")
                .build()));

        assertEquals(ErrorCode.ERROR_REQUEST_INVALID.name(), ex.getErrorCode());
    }

    @Test
    void submitShouldRejectWhenDroneRegistrationIsNotApproved() throws Exception {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(flightApprovalRepository.findByPlanId("plan-1")).thenReturn(Optional.empty());
        when(pilotRegistrationService.getByPilotId("pilot-1")).thenReturn(PilotRegistrationStatusDto.builder()
                .pilotId("pilot-1")
                .status(RegistrationStatus.APPROVED)
                .build());
        when(droneRegistrationService.getByDroneId("drone-1")).thenReturn(DroneRegistrationStatusDto.builder()
                .droneId("drone-1")
                .status(RegistrationStatus.SUSPENDED)
                .build());

        InternalException ex = assertThrows(InternalException.class, () -> flightApprovalService.submit(FlightApprovalSubmitRequest.builder()
                .planId("plan-1")
                .missionId("mission-1")
                .droneId("drone-1")
                .pilotId("pilot-1")
                .build()));

        assertEquals(ErrorCode.ERROR_REQUEST_INVALID.name(), ex.getErrorCode());
    }
}
