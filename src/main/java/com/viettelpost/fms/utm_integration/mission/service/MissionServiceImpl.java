package com.viettelpost.fms.utm_integration.mission.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalStatusDto;
import com.viettelpost.fms.utm_integration.approval.service.FlightApprovalService;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.mission.client.UtmMissionClient;
import com.viettelpost.fms.utm_integration.mission.domain.MissionEntity;
import com.viettelpost.fms.utm_integration.mission.domain.MissionState;
import com.viettelpost.fms.utm_integration.mission.dto.MissionEmergencyRequest;
import com.viettelpost.fms.utm_integration.mission.dto.MissionEventRequest;
import com.viettelpost.fms.utm_integration.mission.dto.MissionStatusDto;
import com.viettelpost.fms.utm_integration.mission.dto.UtmMissionEventRequest;
import com.viettelpost.fms.utm_integration.mission.repository.MissionRepository;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final UtmMissionClient utmMissionClient;
    private final UtmSessionService utmSessionService;
    private final FlightApprovalService flightApprovalService;

    @Override
    @Transactional
    public MissionStatusDto reportAirborne(MissionEventRequest request) throws I18nException {
        UtmSessionContextDto session = requireConnectedSession();
        requireApprovedApproval(request.getPlanId());
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.AIRBORNE);

        utmMissionClient.reportAirborne(toClientRequest(session, request, null));

        mission.setPlanId(request.getPlanId());
        mission.setDroneId(request.getDroneId());
        mission.setState(MissionState.AIRBORNE);
        mission.setAirborneAt(new Date());
        return toDto(missionRepository.save(mission));
    }

    @Override
    @Transactional
    public MissionStatusDto reportLanding(MissionEventRequest request) throws I18nException {
        UtmSessionContextDto session = requireConnectedSession();
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.LANDING);

        utmMissionClient.reportLanding(toClientRequest(session, request, null));

        mission.setPlanId(request.getPlanId());
        mission.setDroneId(request.getDroneId());
        mission.setState(MissionState.LANDING);
        mission.setLandingAt(new Date());
        return toDto(missionRepository.save(mission));
    }

    @Override
    @Transactional
    public MissionStatusDto reportCompletion(MissionEventRequest request) throws I18nException {
        UtmSessionContextDto session = requireConnectedSession();
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.COMPLETED);

        utmMissionClient.reportCompletion(toClientRequest(session, request, null));

        mission.setPlanId(request.getPlanId());
        mission.setDroneId(request.getDroneId());
        mission.setState(MissionState.COMPLETED);
        mission.setCompletedAt(new Date());
        return toDto(missionRepository.save(mission));
    }

    @Override
    @Transactional
    public MissionStatusDto reportEmergency(MissionEmergencyRequest request) throws I18nException {
        UtmSessionContextDto session = requireConnectedSession();
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.EMERGENCY);

        utmMissionClient.reportEmergency(toClientRequest(session, request, request.getEmergencyReason()));

        mission.setPlanId(request.getPlanId());
        mission.setDroneId(request.getDroneId());
        mission.setState(MissionState.EMERGENCY);
        mission.setEmergencyFlag(true);
        mission.setEmergencyReason(request.getEmergencyReason());
        return toDto(missionRepository.save(mission));
    }

    private MissionEntity getOrCreateMission(MissionEventRequest request) {
        return missionRepository.findByMissionId(request.getMissionId())
                .orElseGet(() -> MissionEntity.builder()
                        .missionId(request.getMissionId())
                        .planId(request.getPlanId())
                        .droneId(request.getDroneId())
                        .state(MissionState.READY)
                        .emergencyFlag(false)
                        .build());
    }

    private UtmSessionContextDto requireConnectedSession() throws InternalException {
        UtmSessionContextDto sessionContext = utmSessionService.getCurrentSessionContext();
        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
        return sessionContext;
    }

    private void requireApprovedApproval(String planId) throws I18nException {
        FlightApprovalStatusDto approval = flightApprovalService.getByPlanId(planId);
        if (!ApprovalStatus.APPROVED.equals(approval.getStatus())) {
            throw new InternalException(ErrorCode.ERROR_APPROVAL_NOT_APPROVED);
        }
    }

    private void validateTransition(MissionState currentState, MissionState targetState) throws InternalException {
        boolean valid = switch (targetState) {
            case AIRBORNE -> MissionState.PLANNED.equals(currentState) || MissionState.READY.equals(currentState);
            case LANDING -> MissionState.AIRBORNE.equals(currentState);
            case COMPLETED -> MissionState.LANDING.equals(currentState);
            case EMERGENCY -> !MissionState.COMPLETED.equals(currentState);
            default -> false;
        };

        if (!valid) {
            throw new InternalException(ErrorCode.ERROR_MISSION_TRANSITION_INVALID);
        }
    }

    private UtmMissionEventRequest toClientRequest(UtmSessionContextDto session,
                                                   MissionEventRequest request,
                                                   String emergencyReason) {
        return UtmMissionEventRequest.builder()
                .sessionId(session.sessionId())
                .token(session.token())
                .missionId(request.getMissionId())
                .planId(request.getPlanId())
                .droneId(request.getDroneId())
                .emergencyReason(emergencyReason)
                .build();
    }

    private MissionStatusDto toDto(MissionEntity mission) {
        return MissionStatusDto.builder()
                .id(mission.getId())
                .missionId(mission.getMissionId())
                .planId(mission.getPlanId())
                .droneId(mission.getDroneId())
                .state(mission.getState())
                .airborneAt(mission.getAirborneAt())
                .landingAt(mission.getLandingAt())
                .completedAt(mission.getCompletedAt())
                .emergencyFlag(mission.isEmergencyFlag())
                .emergencyReason(mission.getEmergencyReason())
                .build();
    }
}