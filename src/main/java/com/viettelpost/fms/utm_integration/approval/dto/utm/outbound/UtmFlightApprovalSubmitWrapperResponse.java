package com.viettelpost.fms.utm_integration.approval.dto.utm.outbound;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UtmFlightApprovalSubmitWrapperResponse {

    private Integer code;
    private String message;
    private UtmFlightApprovalSubmitResponse data;
}