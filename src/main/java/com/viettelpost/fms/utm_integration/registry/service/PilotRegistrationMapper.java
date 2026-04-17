package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.utm_integration.registry.client.PilotRegistryRecord;
import com.viettelpost.fms.utm_integration.registry.client.PilotRegistryUpsertRequest;
import com.viettelpost.fms.utm_integration.registry.domain.PilotRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationSyncStatus;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationSubmitRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PilotRegistrationMapper {
    public PilotRegistryUpsertRequest toUpsertRequest(PilotRegistrationSubmitRequest request) {
        return new PilotRegistryUpsertRequest(
                request.getFullName(), request.getDateOfBirth(), request.getPersonalIdNumber(), request.getPersonalIdType(),
                request.getPhoneNumber(), request.getContactAddress(), request.getCityCode(), request.getDistrictCode(),
                request.getWardCode(), request.getLicenseNumber(), request.getLicenseClass(), request.getIssuedDate(),
                request.getIssuedBy(), request.getExpiryDate(), request.getLicenseImageUrl(), request.getStatus());
    }

    public void applySubmitRequest(PilotRegistrationEntity entity, PilotRegistrationSubmitRequest request) {
        entity.setPersonalIdNumber(request.getPersonalIdNumber());
        entity.setLicenseNumber(request.getLicenseNumber());
        entity.setPhoneNumber(request.getPhoneNumber());
    }

    public void applyUtmRecord(PilotRegistrationEntity entity, PilotRegistryRecord record) {
        entity.setUtmPilotId(record.id());
        entity.setStatus(null);
        entity.setLegitVerify(record.legitVerify());
        entity.setNote(record.note());
        entity.setOrganizationId(record.organizationId());
        entity.setLicenseNumber(record.licenseNumber());
        entity.setPersonalIdNumber(record.personalIdNumber());
        entity.setPhoneNumber(record.phoneNumber());
        entity.setRejectReason(null);
    }

    public PilotRegistrationStatusDto toDto(PilotRegistrationEntity registration) {
        return PilotRegistrationStatusDto.builder()
                .id(registration.getId())
                .utmPilotId(registration.getUtmPilotId())
                .syncStatus(registration.getSyncStatus())
                .status(registration.getStatus())
                .submittedAt(registration.getSubmittedAt())
                .approvedAt(registration.getApprovedAt())
                .rejectedAt(registration.getRejectedAt())
                .rejectReason(registration.getRejectReason())
                .legitVerify(registration.getLegitVerify())
                .note(registration.getNote())
                .organizationId(registration.getOrganizationId())
                .licenseNumber(registration.getLicenseNumber())
                .personalIdNumber(registration.getPersonalIdNumber())
                .phoneNumber(registration.getPhoneNumber())
                .lastSyncedAt(registration.getLastSyncedAt())
                .errorCode(registration.getSyncErrorCode())
                .errorMessage(registration.getSyncErrorMessage())
                .build();
    }

    public PilotRegistrationStatusDto toUtmDto(PilotRegistryRecord record) {
        return PilotRegistrationStatusDto.builder()
                .utmPilotId(record.id())
                .syncStatus(RegistrationSyncStatus.SYNCED)
                .status(null)
                .legitVerify(record.legitVerify())
                .note(record.note())
                .organizationId(record.organizationId())
                .licenseNumber(record.licenseNumber())
                .personalIdNumber(record.personalIdNumber())
                .phoneNumber(record.phoneNumber())
                .lastSyncedAt(new Date())
                .build();
    }
}
