package com.viettelpost.fms.utm_integration.approval.service;

import com.viettelpost.fms.utm_integration.approval.dto.api.FlightApprovalStatusResponse;
import com.viettelpost.fms.utm_integration.approval.dto.api.FlightApprovalSubmitResponse;
import com.viettelpost.fms.utm_integration.approval.dto.api.UtmFlightApprovalRequest;
import com.viettelpost.fms.utm_integration.approval.dto.utm.inbound.UtmFlightApprovalStatusMessage;

public interface FlightApprovalService {

    FlightApprovalSubmitResponse submit(UtmFlightApprovalRequest request);

    FlightApprovalStatusResponse getByPlanId(String planId);

    void handleUtmStatus(UtmFlightApprovalStatusMessage message);
}