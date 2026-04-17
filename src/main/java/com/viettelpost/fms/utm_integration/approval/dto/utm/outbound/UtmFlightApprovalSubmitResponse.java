package com.viettelpost.fms.utm_integration.approval.dto.utm.outbound;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UtmFlightApprovalSubmitResponse {

    private String applicationId;
    private String requestId;
    private String status;
}