package com.viettelpost.fms.utm_integration.approval.client;

import com.viettelpost.fms.utm_integration.approval.dto.UtmApprovalSubmissionRequest;
import com.viettelpost.fms.utm_integration.approval.dto.UtmApprovalSubmissionResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class StubUtmApprovalClient implements UtmApprovalClient {

    @Override
    public UtmApprovalSubmissionResult submit(UtmApprovalSubmissionRequest request) {
        return UtmApprovalSubmissionResult.builder()
                .utmRequestId("stub-approval-" + UUID.randomUUID())
                .requestedAt(new Date())
                .build();
    }
}