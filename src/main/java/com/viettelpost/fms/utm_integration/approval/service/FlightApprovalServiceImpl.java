package com.viettelpost.fms.utm_integration.approval.service;

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
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlightApprovalServiceImpl implements FlightApprovalService {

    private final FlightApprovalRepository flightApprovalRepository;
    private final UtmApprovalClient utmApprovalClient;
    private final UtmSessionService utmSessionService;

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