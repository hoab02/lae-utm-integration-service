package com.viettelpost.fms.utm_integration.approval.client;

import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitRequest;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitResponse;

public interface UtmFlightApprovalClient {

    UtmFlightApprovalSubmitResponse submit(
            UtmFlightApprovalSubmitRequest request,
            String authorizationHeaderValue
    );
}