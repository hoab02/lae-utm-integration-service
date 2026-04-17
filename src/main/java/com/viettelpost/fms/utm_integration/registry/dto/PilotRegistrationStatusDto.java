package com.viettelpost.fms.utm_integration.registry.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationSyncStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PilotRegistrationStatusDto {

    private String id;

    private String utmPilotId;

    private RegistrationSyncStatus syncStatus;

    private RegistrationStatus status;

    private Date submittedAt;

    private Date approvedAt;

    private Date rejectedAt;

    private String rejectReason;

    private String legitVerify;

    private String note;

    private String organizationId;

    private String licenseNumber;

    private String personalIdNumber;

    private String phoneNumber;

    private Date lastSyncedAt;

    private String errorCode;

    private String errorMessage;
}
