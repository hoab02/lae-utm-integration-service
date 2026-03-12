package com.viettelpost.fms.utm_integration.approval.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalStatusDto;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalSubmitRequest;
import com.viettelpost.fms.utm_integration.approval.service.FlightApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/utm/flight-approvals")
@Tag(name = "Flight Approval")
public class FlightApprovalController {

    private final FlightApprovalService flightApprovalService;

    @PostMapping
    @Operation(summary = "Submit a flight approval request to UTM and persist the current approval state")
    public ResponseEntity<FlightApprovalStatusDto> submit(@Valid @RequestBody FlightApprovalSubmitRequest request)
            throws I18nException {
        return ResponseEntity.ok(flightApprovalService.submit(request));
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get the current approval state by plan id")
    public ResponseEntity<FlightApprovalStatusDto> getByPlanId(@PathVariable String planId) throws I18nException {
        return ResponseEntity.ok(flightApprovalService.getByPlanId(planId));
    }
}