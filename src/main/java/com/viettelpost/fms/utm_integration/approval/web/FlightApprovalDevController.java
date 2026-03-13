package com.viettelpost.fms.utm_integration.approval.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.approval.dto.FlightApprovalStatusDto;
import com.viettelpost.fms.utm_integration.approval.service.FlightApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/utm/dev/flight-approvals")
@ConditionalOnProperty(name = "utm.approval.dev-update-enabled", havingValue = "true")
@Tag(name = "Flight Approval Dev")
public class FlightApprovalDevController {

    private final FlightApprovalService flightApprovalService;

    @PostMapping("/{planId}/approve")
    @Operation(summary = "Mark an existing submitted approval as approved for dev/test flows")
    public ResponseEntity<FlightApprovalStatusDto> markApproved(@PathVariable String planId) throws I18nException {
        return ResponseEntity.ok(flightApprovalService.markApproved(planId));
    }
}
