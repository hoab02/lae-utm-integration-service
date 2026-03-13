package com.viettelpost.fms.utm_integration.registry.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.service.PilotRegistrationService;
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
@RequestMapping("/internal/utm/pilots")
@Tag(name = "Pilot Registration")
public class PilotRegistrationController {

    private final PilotRegistrationService pilotRegistrationService;

    @PostMapping
    @Operation(summary = "Submit pilot registration to UTM and persist the current state")
    public ResponseEntity<PilotRegistrationStatusDto> submit(@Valid @RequestBody PilotRegistrationSubmitRequest request)
            throws I18nException {
        return ResponseEntity.ok(pilotRegistrationService.submit(request));
    }

    @GetMapping("/{pilotId}")
    @Operation(summary = "Get current pilot registration state by pilot id")
    public ResponseEntity<PilotRegistrationStatusDto> getByPilotId(@PathVariable String pilotId) throws I18nException {
        return ResponseEntity.ok(pilotRegistrationService.getByPilotId(pilotId));
    }
}
