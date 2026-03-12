package com.viettelpost.fms.utm_integration.approval.client;

import com.viettelpost.fms.utm_integration.approval.dto.UtmApprovalSubmissionRequest;
import com.viettelpost.fms.utm_integration.approval.dto.UtmApprovalSubmissionResult;

public interface UtmApprovalClient {

    UtmApprovalSubmissionResult submit(UtmApprovalSubmissionRequest request);
}