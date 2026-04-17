package com.viettelpost.fms.utm_integration.mission.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import com.viettelpost.fms.utm_integration.approval.dto.api.FlightApprovalStatusResponse;
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
import com.viettelpost.fms.utm_integration.reliability.idempotency.IdempotencyService;
import com.viettelpost.fms.utm_integration.reliability.retry.RetryExecutor;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionServiceImpl implements MissionService {

    private static final String MISSION_AIRBORNE_OPERATION = "MISSION_AIRBORNE";
    private static final String MISSION_LANDING_OPERATION = "MISSION_LANDING";
    private static final String MISSION_COMPLETION_OPERATION = "MISSION_COMPLETION";
    private static final String MISSION_EMERGENCY_OPERATION = "MISSION_EMERGENCY";

    private final MissionRepository missionRepository;
    private final UtmMissionClient utmMissionClient;
    private final UtmSessionService utmSessionService;
    private final FlightApprovalService flightApprovalService;
    private final IdempotencyService idempotencyService;
    private final RetryExecutor retryExecutor;

    @Override
    @Transactional
    public MissionStatusDto reportAirborne(MissionEventRequest request) throws I18nException {
        String idempotencyKey = MISSION_AIRBORNE_OPERATION + ":" + request.getMissionId();
        log.info("mission_airborne_start missionId={} planId={} droneId={}",
                request.getMissionId(), request.getPlanId(), request.getDroneId());
        if (idempotencyService.hasSucceeded(MISSION_AIRBORNE_OPERATION, idempotencyKey)) {
            log.info("mission_airborne_skipped missionId={} planId={} reason=idempotency_succeeded",
                    request.getMissionId(), request.getPlanId());
            return getExistingMissionStatus(request.getMissionId());
        }

        UtmSessionContextDto session = requireConnectedSession();
        requireApprovedApproval(request.getPlanId());
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.AIRBORNE);
        idempotencyService.begin(MISSION_AIRBORNE_OPERATION, idempotencyKey);

        try {
            log.info("mission_airborne_utm_call missionId={} planId={}",
                    request.getMissionId(), request.getPlanId());
            retryExecutor.execute(() -> {
                utmMissionClient.reportAirborne(toClientRequest(session, request, null));
                return null;
            });

            mission.setPlanId(request.getPlanId());
            mission.setDroneId(request.getDroneId());
            mission.setState(MissionState.AIRBORNE);
            mission.setAirborneAt(new Date());
            MissionStatusDto status = toDto(missionRepository.save(mission));
            idempotencyService.markSucceeded(MISSION_AIRBORNE_OPERATION, idempotencyKey);
            log.info("mission_airborne_success missionId={} planId={} state={}",
                    status.getMissionId(), status.getPlanId(), status.getState());
            return status;
        } catch (RuntimeException ex) {
            idempotencyService.markFailed(MISSION_AIRBORNE_OPERATION, idempotencyKey, ex.getMessage());
            log.error("mission_airborne_failure missionId={} planId={} errorType={}",
                    request.getMissionId(), request.getPlanId(), ex.getClass().getSimpleName(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public MissionStatusDto reportLanding(MissionEventRequest request) throws I18nException {
        String idempotencyKey = MISSION_LANDING_OPERATION + ":" + request.getMissionId();
        log.info("mission_landing_start missionId={} planId={} droneId={}",
                request.getMissionId(), request.getPlanId(), request.getDroneId());
        if (idempotencyService.hasSucceeded(MISSION_LANDING_OPERATION, idempotencyKey)) {
            log.info("mission_landing_skipped missionId={} planId={} reason=idempotency_succeeded",
                    request.getMissionId(), request.getPlanId());
            return getExistingMissionStatus(request.getMissionId());
        }

        UtmSessionContextDto session = requireConnectedSession();
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.LANDING);
        idempotencyService.begin(MISSION_LANDING_OPERATION, idempotencyKey);

        try {
            log.info("mission_landing_utm_call missionId={} planId={}",
                    request.getMissionId(), request.getPlanId());
            retryExecutor.execute(() -> {
                utmMissionClient.reportLanding(toClientRequest(session, request, null));
                return null;
            });

            mission.setPlanId(request.getPlanId());
            mission.setDroneId(request.getDroneId());
            mission.setState(MissionState.LANDING);
            mission.setLandingAt(new Date());
            MissionStatusDto status = toDto(missionRepository.save(mission));
            idempotencyService.markSucceeded(MISSION_LANDING_OPERATION, idempotencyKey);
            log.info("mission_landing_success missionId={} planId={} state={}",
                    status.getMissionId(), status.getPlanId(), status.getState());
            return status;
        } catch (RuntimeException ex) {
            idempotencyService.markFailed(MISSION_LANDING_OPERATION, idempotencyKey, ex.getMessage());
            log.error("mission_landing_failure missionId={} planId={} errorType={}",
                    request.getMissionId(), request.getPlanId(), ex.getClass().getSimpleName(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public MissionStatusDto reportCompletion(MissionEventRequest request) throws I18nException {
        String idempotencyKey = MISSION_COMPLETION_OPERATION + ":" + request.getMissionId();
        log.info("mission_completion_start missionId={} planId={} droneId={}",
                request.getMissionId(), request.getPlanId(), request.getDroneId());
        if (idempotencyService.hasSucceeded(MISSION_COMPLETION_OPERATION, idempotencyKey)) {
            log.info("mission_completion_skipped missionId={} planId={} reason=idempotency_succeeded",
                    request.getMissionId(), request.getPlanId());
            return getExistingMissionStatus(request.getMissionId());
        }

        UtmSessionContextDto session = requireConnectedSession();
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.COMPLETED);
        idempotencyService.begin(MISSION_COMPLETION_OPERATION, idempotencyKey);

        try {
            log.info("mission_completion_utm_call missionId={} planId={}",
                    request.getMissionId(), request.getPlanId());
            retryExecutor.execute(() -> {
                utmMissionClient.reportCompletion(toClientRequest(session, request, null));
                return null;
            });

            mission.setPlanId(request.getPlanId());
            mission.setDroneId(request.getDroneId());
            mission.setState(MissionState.COMPLETED);
            mission.setCompletedAt(new Date());
            MissionStatusDto status = toDto(missionRepository.save(mission));
            idempotencyService.markSucceeded(MISSION_COMPLETION_OPERATION, idempotencyKey);
            log.info("mission_completion_success missionId={} planId={} state={}",
                    status.getMissionId(), status.getPlanId(), status.getState());
            return status;
        } catch (RuntimeException ex) {
            idempotencyService.markFailed(MISSION_COMPLETION_OPERATION, idempotencyKey, ex.getMessage());
            log.error("mission_completion_failure missionId={} planId={} errorType={}",
                    request.getMissionId(), request.getPlanId(), ex.getClass().getSimpleName(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public MissionStatusDto reportEmergency(MissionEmergencyRequest request) throws I18nException {
        String idempotencyKey = MISSION_EMERGENCY_OPERATION + ":" + request.getMissionId();
        log.info("mission_emergency_start missionId={} planId={} droneId={}",
                request.getMissionId(), request.getPlanId(), request.getDroneId());
        if (idempotencyService.hasSucceeded(MISSION_EMERGENCY_OPERATION, idempotencyKey)) {
            log.info("mission_emergency_skipped missionId={} planId={} reason=idempotency_succeeded",
                    request.getMissionId(), request.getPlanId());
            return getExistingMissionStatus(request.getMissionId());
        }

        UtmSessionContextDto session = requireConnectedSession();
        MissionEntity mission = getOrCreateMission(request);
        validateTransition(mission.getState(), MissionState.EMERGENCY);
        idempotencyService.begin(MISSION_EMERGENCY_OPERATION, idempotencyKey);

        try {
            log.info("mission_emergency_utm_call missionId={} planId={}",
                    request.getMissionId(), request.getPlanId());
            retryExecutor.execute(() -> {
                utmMissionClient.reportEmergency(toClientRequest(session, request, request.getEmergencyReason()));
                return null;
            });

            mission.setPlanId(request.getPlanId());
            mission.setDroneId(request.getDroneId());
            mission.setState(MissionState.EMERGENCY);
            mission.setEmergencyFlag(true);
            mission.setEmergencyReason(request.getEmergencyReason());
            MissionStatusDto status = toDto(missionRepository.save(mission));
            idempotencyService.markSucceeded(MISSION_EMERGENCY_OPERATION, idempotencyKey);
            log.info("mission_emergency_success missionId={} planId={} state={}",
                    status.getMissionId(), status.getPlanId(), status.getState());
            return status;
        } catch (RuntimeException ex) {
            idempotencyService.markFailed(MISSION_EMERGENCY_OPERATION, idempotencyKey, ex.getMessage());
            log.error("mission_emergency_failure missionId={} planId={} errorType={}",
                    request.getMissionId(), request.getPlanId(), ex.getClass().getSimpleName(), ex);
            throw ex;
        }
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

    private MissionStatusDto getExistingMissionStatus(String missionId) throws InternalException {
        MissionEntity mission = missionRepository.findByMissionId(missionId)
                .orElseThrow(() -> new InternalException(ErrorCode.ERROR_REQUEST_INVALID));
        return toDto(mission);
    }

    private UtmSessionContextDto requireConnectedSession() throws InternalException {
        UtmSessionContextDto sessionContext = utmSessionService.getCurrentSessionContext();
        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
        return sessionContext;
    }

    private void requireApprovedApproval(String planId) throws I18nException {
        FlightApprovalStatusResponse approval = flightApprovalService.getByPlanId(planId);
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
                .sessionId(session.accessToken())
                .token(session.accessToken())
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