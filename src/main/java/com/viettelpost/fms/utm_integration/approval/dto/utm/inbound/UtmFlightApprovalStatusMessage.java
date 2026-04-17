package com.viettelpost.fms.utm_integration.approval.dto.utm.inbound;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UtmFlightApprovalStatusMessage {

    private String applicationId;
    private String requestId;
    private String planId;
    private String missionId;
    private String status;
    private String reason;
    private String updatedAt;
}