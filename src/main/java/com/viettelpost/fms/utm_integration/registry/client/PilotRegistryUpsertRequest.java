package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Date;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PilotRegistryUpsertRequest(
        String fullName,
        String dateOfBirth,
        String personalIdNumber,
        String personalIdType,
        String phoneNumber,
        String contactAddress,
        String cityCode,
        String districtCode,
        String wardCode,
        String licenseNumber,
        String licenseClass,
        String issuedDate,
        String issuedBy,
        String expiryDate,
        String licenseImageUrl,
        String status
) {
}