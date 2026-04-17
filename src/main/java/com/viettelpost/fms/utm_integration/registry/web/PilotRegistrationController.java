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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/pilots")
@Tag(name = "Pilot Registration")
public class PilotRegistrationController {

    private final PilotRegistrationService pilotRegistrationService;

    @PostMapping
    @Operation(summary = "Submit pilot registration sync command to UTM and persist the current local sync state")
    public ResponseEntity<PilotRegistrationStatusDto> submit(@Valid @RequestBody PilotRegistrationSubmitRequest request)
            throws I18nException {
        return ResponseEntity.ok(pilotRegistrationService.submit(request));
    }

    @PutMapping("/{personalIdNumber}")
    @Operation(summary = "Update or resync an existing local pilot registration state to UTM")
    public ResponseEntity<PilotRegistrationStatusDto> update(@PathVariable String personalIdNumber,
                                                             @Valid @RequestBody PilotRegistrationSubmitRequest request)
            throws I18nException {
        return ResponseEntity.ok(pilotRegistrationService.update(personalIdNumber, request));
    }

    @GetMapping("/utm/{utmPilotId}")
    @Operation(summary = "Get pilot registration data directly from UTM by UTM id")
    public ResponseEntity<PilotRegistrationStatusDto> getByUtmPilotId(@PathVariable String utmPilotId) throws I18nException {
        return ResponseEntity.ok(pilotRegistrationService.getByUtmPilotId(utmPilotId));
    }

    @GetMapping("/utm")
    @Operation(summary = "Search pilot registration data directly from UTM by business keys")
    public ResponseEntity<List<PilotRegistrationStatusDto>> searchUtm(@RequestParam(required = false) String personalIdNumber,
                                                                      @RequestParam(required = false) String licenseNumber,
                                                                      @RequestParam(required = false) String phoneNumber)
            throws I18nException {
        return ResponseEntity.ok(pilotRegistrationService.searchUtm(personalIdNumber, licenseNumber, phoneNumber));
    }

    @GetMapping("/{personalIdNumber}")
    @Operation(summary = "Get current local pilot registration sync state by local pilot personal id number")
    public ResponseEntity<PilotRegistrationStatusDto> getByPersonalIdNumber(@PathVariable String personalIdNumber) throws I18nException {
        return ResponseEntity.ok(pilotRegistrationService.getByPersonalIdNumber(personalIdNumber));
    }
}
