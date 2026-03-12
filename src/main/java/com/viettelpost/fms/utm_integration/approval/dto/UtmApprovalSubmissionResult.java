package com.viettelpost.fms.utm_integration.approval.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record UtmApprovalSubmissionResult(
        String utmRequestId,
        Date requestedAt
) {
}