package com.viettelpost.fms.utm_integration.approval.web;

import com.viettelpost.fms.utm_integration.approval.dto.api.FlightApprovalStatusResponse;
import com.viettelpost.fms.utm_integration.approval.dto.api.FlightApprovalSubmitResponse;
import com.viettelpost.fms.utm_integration.approval.dto.api.UtmFlightApprovalRequest;
import com.viettelpost.fms.utm_integration.approval.service.FlightApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/utm/flight-approvals")
public class FlightApprovalController {

    private final FlightApprovalService flightApprovalService;

    @PostMapping
    public FlightApprovalSubmitResponse submit(@Valid @RequestBody UtmFlightApprovalRequest request) {
        return flightApprovalService.submit(request);
    }

    @GetMapping("/{planId}")
    public FlightApprovalStatusResponse getByPlanId(@PathVariable String planId) {
        return flightApprovalService.getByPlanId(planId);
    }
}