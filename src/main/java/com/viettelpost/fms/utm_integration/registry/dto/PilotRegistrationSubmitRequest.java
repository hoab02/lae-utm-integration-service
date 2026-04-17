package com.viettelpost.fms.utm_integration.registry.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
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
public class PilotRegistrationSubmitRequest {

    private String personalIdNumber;
    private String fullName;
    private String dateOfBirth;
    private String personalIdType;
    private String phoneNumber;
    private String contactAddress;
    private String cityCode;
    private String districtCode;
    private String wardCode;
    private String licenseNumber;
    private String licenseClass;
    private String issuedDate;
    private String issuedBy;
    private String expiryDate;
    private String licenseImageUrl;
    private String status;
}
