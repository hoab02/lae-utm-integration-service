package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.client.PilotRegistrySubmissionRequest;
import com.viettelpost.fms.utm_integration.registry.client.PilotRegistrySubmissionResult;
import com.viettelpost.fms.utm_integration.registry.client.UtmPilotRegistryClient;
import com.viettelpost.fms.utm_integration.registry.domain.PilotRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.repository.PilotRegistrationRepository;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PilotRegistrationServiceImpl implements PilotRegistrationService {

    private final PilotRegistrationRepository pilotRegistrationRepository;
    private final UtmPilotRegistryClient utmPilotRegistryClient;
    private final UtmSessionService utmSessionService;

    @Override
    @Transactional
    public PilotRegistrationStatusDto submit(PilotRegistrationSubmitRequest request) throws InternalException {
        UtmSessionContextDto session = requireConnectedSession();
        PilotRegistrationEntity registration = pilotRegistrationRepository.findByPilotId(request.getPilotId())
                .orElseGet(() -> PilotRegistrationEntity.builder()
                        .pilotId(request.getPilotId())
                        .status(RegistrationStatus.DRAFT)
                        .build());

        validateSubmitTransition(registration);

        PilotRegistrySubmissionResult submitResult = utmPilotRegistryClient.submit(new PilotRegistrySubmissionRequest(
                session.sessionId(),
                session.token(),
                request.getPilotId()
        ));

        registration.setUtmPilotId(submitResult.utmPilotId());
        registration.setStatus(RegistrationStatus.SUBMITTED);
        registration.setSubmittedAt(new Date());
        registration.setApprovedAt(null);
        registration.setRejectedAt(null);
        registration.setRejectReason(null);

        return toDto(pilotRegistrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public PilotRegistrationStatusDto getByPilotId(String pilotId) throws InternalException {
        return pilotRegistrationRepository.findByPilotId(pilotId)
                .map(this::toDto)
                .orElseThrow(() -> new InternalException(ErrorCode.ERROR_PILOT_REGISTRATION_NOT_FOUND));
    }

    private UtmSessionContextDto requireConnectedSession() throws InternalException {
        UtmSessionContextDto sessionContext = utmSessionService.getCurrentSessionContext();
        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
        return sessionContext;
    }

    private void validateSubmitTransition(PilotRegistrationEntity registration) throws InternalException {
        if (RegistrationStatus.SUBMITTED.equals(registration.getStatus())
                || RegistrationStatus.APPROVED.equals(registration.getStatus())
                || RegistrationStatus.REJECTED.equals(registration.getStatus())
                || RegistrationStatus.SUSPENDED.equals(registration.getStatus())) {
            throw new InternalException(ErrorCode.ERROR_REGISTRATION_ALREADY_SUBMITTED);
        }
    }

    private PilotRegistrationStatusDto toDto(PilotRegistrationEntity registration) {
        return PilotRegistrationStatusDto.builder()
                .id(registration.getId())
                .pilotId(registration.getPilotId())
                .utmPilotId(registration.getUtmPilotId())
                .status(registration.getStatus())
                .submittedAt(registration.getSubmittedAt())
                .approvedAt(registration.getApprovedAt())
                .rejectedAt(registration.getRejectedAt())
                .rejectReason(registration.getRejectReason())
                .build();
    }
}
