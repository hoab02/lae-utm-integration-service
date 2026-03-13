package com.viettelpost.fms.utm_integration.telemetry.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;
import com.viettelpost.fms.utm_integration.telemetry.service.TelemetryForwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/utm/telemetry")
@Tag(name = "Telemetry")
public class TelemetryController {

    private final TelemetryForwardService telemetryForwardService;

    @PostMapping
    @Operation(summary = "Forward telemetry to UTM through the outbound adapter")
    public ResponseEntity<Void> forward(@Valid @RequestBody TelemetryMessage message) throws I18nException {
        telemetryForwardService.forward(message);
        return ResponseEntity.ok().build();
    }
}
