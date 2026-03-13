package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistrySubmissionRequest;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistrySubmissionResult;
import com.viettelpost.fms.utm_integration.registry.client.UtmDroneRegistryClient;
import com.viettelpost.fms.utm_integration.registry.domain.DroneRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.repository.DroneRegistrationRepository;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DroneRegistrationServiceImpl implements DroneRegistrationService {

    private final DroneRegistrationRepository droneRegistrationRepository;
    private final UtmDroneRegistryClient utmDroneRegistryClient;
    private final UtmSessionService utmSessionService;

    @Override
    @Transactional
    public DroneRegistrationStatusDto submit(DroneRegistrationSubmitRequest request) throws InternalException {
        UtmSessionContextDto session = requireConnectedSession();
        DroneRegistrationEntity registration = droneRegistrationRepository.findByDroneId(request.getDroneId())
                .orElseGet(() -> DroneRegistrationEntity.builder()
                        .droneId(request.getDroneId())
                        .status(RegistrationStatus.DRAFT)
                        .build());

        validateSubmitTransition(registration);

        DroneRegistrySubmissionResult submitResult = utmDroneRegistryClient.submit(new DroneRegistrySubmissionRequest(
                session.sessionId(),
                session.token(),
                request.getDroneId()
        ));

        registration.setUtmDroneId(submitResult.utmDroneId());
        registration.setStatus(RegistrationStatus.SUBMITTED);
        registration.setSubmittedAt(new Date());
        registration.setApprovedAt(null);
        registration.setRejectedAt(null);
        registration.setRejectReason(null);

        return toDto(droneRegistrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public DroneRegistrationStatusDto getByDroneId(String droneId) throws InternalException {
        return droneRegistrationRepository.findByDroneId(droneId)
                .map(this::toDto)
                .orElseThrow(() -> new InternalException(ErrorCode.ERROR_DRONE_REGISTRATION_NOT_FOUND));
    }

    private UtmSessionContextDto requireConnectedSession() throws InternalException {
        UtmSessionContextDto sessionContext = utmSessionService.getCurrentSessionContext();
        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new InternalException(ErrorCode.ERROR_SESSION_NOT_CONNECTED);
        }
        return sessionContext;
    }

    private void validateSubmitTransition(DroneRegistrationEntity registration) throws InternalException {
        if (RegistrationStatus.SUBMITTED.equals(registration.getStatus())
                || RegistrationStatus.APPROVED.equals(registration.getStatus())
                || RegistrationStatus.REJECTED.equals(registration.getStatus())
                || RegistrationStatus.SUSPENDED.equals(registration.getStatus())) {
            throw new InternalException(ErrorCode.ERROR_REGISTRATION_ALREADY_SUBMITTED);
        }
    }

    private DroneRegistrationStatusDto toDto(DroneRegistrationEntity registration) {
        return DroneRegistrationStatusDto.builder()
                .id(registration.getId())
                .droneId(registration.getDroneId())
                .utmDroneId(registration.getUtmDroneId())
                .status(registration.getStatus())
                .submittedAt(registration.getSubmittedAt())
                .approvedAt(registration.getApprovedAt())
                .rejectedAt(registration.getRejectedAt())
                .rejectReason(registration.getRejectReason())
                .build();
    }
}
