package com.viettelpost.fms.utm_integration.approval.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.approval.client.UtmApprovalClient;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import com.viettelpost.fms.utm_integration.approval.domain.FlightApprovalEntity;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalStatusDto;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalSubmitRequest;
import com.viettelpost.fms.utm_integration.approval.dto.UtmApprovalSubmissionRequest;
import com.viettelpost.fms.utm_integration.approval.dto.UtmApprovalSubmissionResult;
import com.viettelpost.fms.utm_integration.approval.repository.FlightApprovalRepository;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.service.DroneRegistrationService;
import com.viettelpost.fms.utm_integration.registry.service.PilotRegistrationService;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FlightApprovalServiceImpl implements FlightApprovalService {

    private final FlightApprovalRepository flightApprovalRepository;
    private final UtmApprovalClient utmApprovalClient;
    private final UtmSessionService utmSessionService;
    private final PilotRegistrationService pilotRegistrationService;
    private final DroneRegistrationService droneRegistrationService;

    @Override
    @Transactional
    public FlightApprovalStatusDto submit(FlightApprovalSubmitRequest request) throws InternalException {
        UtmSessionContextDto session = requireConnectedSession();
        FlightApprovalEntity approval = flightApprovalRepository.findByPlanId(request.getPlanId())
                .orElseGet(() -> FlightApprovalEntity.builder()
                        .planId(request.getPlanId())
                        .status(ApprovalStatus.DRAFT)
                        .build());

        validateSubmitTransition(approval);
        validateRegistryApprovalPrerequisites(request);

        approval.setMissionId(request.getMissionId());
        approval.setDroneId(request.getDroneId());
        approval.setPilotId(request.getPilotId());
        approval.setApprovedAt(null);
        approval.setRejectedAt(null);
        approval.setRejectReason(null);

        UtmApprovalSubmissionResult submitResult = utmApprovalClient.submit(UtmApprovalSubmissionRequest.builder()
                .sessionId(session.sessionId())
                .token(session.token())
                .planId(request.getPlanId())
                .missionId(request.getMissionId())
                .droneId(request.getDroneId())
                .pilotId(request.getPilotId())
                .build());

        approval.setUtmRequestId(submitResult.utmRequestId());
        approval.setRequestedAt(submitResult.requestedAt());
        approval.setStatus(ApprovalStatus.SUBMITTED);

        return toDto(flightApprovalRepository.save(approval));
    }

    @Override
    @Transactional(readOnly = true)
    public FlightApprovalStatusDto getByPlanId(String planId) throws InternalException {
        return flightApprovalRepository.findByPlanId(planId)
                .map(this::toDto)
                .orElseThrow(() -> new InternalException(ErrorCode.ERROR_APPROVAL_NOT_FOUND));
    }

    @Override
    @Transactional
    public FlightApprovalStatusDto markApproved(String planId) throws InternalException {
        FlightApprovalEntity approval = flightApprovalRepository.findByPlanId(planId)
                .orElseThrow(() -> new InternalException(ErrorCode.ERROR_APPROVAL_NOT_FOUND));

        validateApproveTransition(approval);

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setApprovedAt(new Date());
        approval.setRejectedAt(null);
        approval.setRejectReason(null);

        return toDto(flightApprovalRepository.save(approval));
    }

    private UtmSessionContextDto requireConnectedSession() throws InternalException {
        UtmSessionContextDto sessionContext = utmSessionService.getCurrentSessionContext();
        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
        return sessionContext;
    }

    private void validateSubmitTransition(FlightApprovalEntity approval) throws InternalException {
        if (ApprovalStatus.SUBMITTED.equals(approval.getStatus())
                || ApprovalStatus.APPROVED.equals(approval.getStatus())) {
            throw new InternalException(ErrorCode.ERROR_REQUEST_INVALID);
        }
    }

    private void validateRegistryApprovalPrerequisites(FlightApprovalSubmitRequest request) throws InternalException {
        try {
            if (!RegistrationStatus.APPROVED.equals(pilotRegistrationService.getByPilotId(request.getPilotId()).getStatus())
                    || !RegistrationStatus.APPROVED.equals(droneRegistrationService.getByDroneId(request.getDroneId()).getStatus())) {
                throw new InternalException(ErrorCode.ERROR_REQUEST_INVALID);
            }
        } catch (I18nException ex) {
            if (ex instanceof InternalException internalException
                    && ErrorCode.ERROR_REQUEST_INVALID.name().equals(internalException.getErrorCode())) {
                throw internalException;
            }
            throw new InternalException(ErrorCode.ERROR_REQUEST_INVALID);
        }
    }

    private void validateApproveTransition(FlightApprovalEntity approval) throws InternalException {
        if (!ApprovalStatus.SUBMITTED.equals(approval.getStatus())) {
            throw new InternalException(ErrorCode.ERROR_REQUEST_INVALID);
        }
    }

    private FlightApprovalStatusDto toDto(FlightApprovalEntity approval) {
        return FlightApprovalStatusDto.builder()
                .id(approval.getId())
                .planId(approval.getPlanId())
                .missionId(approval.getMissionId())
                .droneId(approval.getDroneId())
                .pilotId(approval.getPilotId())
                .utmRequestId(approval.getUtmRequestId())
                .status(approval.getStatus())
                .requestedAt(approval.getRequestedAt())
                .approvedAt(approval.getApprovedAt())
                .rejectedAt(approval.getRejectedAt())
                .rejectReason(approval.getRejectReason())
                .build();
    }
}
