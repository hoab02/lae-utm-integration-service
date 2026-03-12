package com.viettelpost.fms.utm_integration.approval.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalStatusDto;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalSubmitRequest;

public interface FlightApprovalService {

    FlightApprovalStatusDto submit(FlightApprovalSubmitRequest request) throws I18nException;

    FlightApprovalStatusDto getByPlanId(String planId) throws I18nException;
}