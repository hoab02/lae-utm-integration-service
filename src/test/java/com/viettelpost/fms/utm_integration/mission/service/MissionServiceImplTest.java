package com.viettelpost.fms.utm_integration.mission.service;

import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalStatusDto;
import com.viettelpost.fms.utm_integration.approval.service.FlightApprovalService;
import com.viettelpost.fms.utm_integration.mission.client.UtmMissionClient;
import com.viettelpost.fms.utm_integration.mission.domain.MissionEntity;
import com.viettelpost.fms.utm_integration.mission.domain.MissionState;
import com.viettelpost.fms.utm_integration.mission.dto.MissionEventRequest;
import com.viettelpost.fms.utm_integration.mission.repository.MissionRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionServiceImplTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private UtmMissionClient utmMissionClient;

    @Mock
    private UtmSessionService utmSessionService;

    @Mock
    private FlightApprovalService flightApprovalService;

    @InjectMocks
    private MissionServiceImpl missionService;

    @Test
    void reportAirborneShouldSucceedWhenApprovalIsApproved() throws Exception {
        when(utmSessionService.getCurrentSessionContext()).thenReturn(UtmSessionContextDto.builder()
                .sessionId("session-1")
                .token("token-1")
                .status(SessionStatus.CONNECTED)
                .build());
        when(flightApprovalService.getByPlanId("plan-1")).thenReturn(FlightApprovalStatusDto.builder()
                .planId("plan-1")
                .missionId("mission-1")
                .droneId("drone-1")
                .status(ApprovalStatus.APPROVED)
                .build());
        when(missionRepository.findByMissionId("mission-1")).thenReturn(Optional.empty());
        when(missionRepository.save(any(MissionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = missionService.reportAirborne(MissionEventRequest.builder()
                .missionId("mission-1")
                .planId("plan-1")
                .droneId("drone-1")
                .build());

        assertEquals(MissionState.AIRBORNE, result.getState());
        assertEquals("mission-1", result.getMissionId());
        assertEquals("plan-1", result.getPlanId());
        assertEquals("drone-1", result.getDroneId());
        assertNotNull(result.getAirborneAt());
        assertFalse(result.isEmergencyFlag());
        verify(utmMissionClient).reportAirborne(any());
        verify(missionRepository).save(any(MissionEntity.class));
    }
}
