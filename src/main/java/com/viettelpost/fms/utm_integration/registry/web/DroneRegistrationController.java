package com.viettelpost.fms.utm_integration.registry.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.service.DroneRegistrationService;
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
@RequestMapping("/internal/drones")
@Tag(name = "Drone Registration")
public class DroneRegistrationController {

    private final DroneRegistrationService droneRegistrationService;

    @PostMapping
    @Operation(summary = "Submit drone registration sync command to UTM and persist the current local sync state")
    public ResponseEntity<DroneRegistrationStatusDto> submit(@Valid @RequestBody DroneRegistrationSubmitRequest request)
            throws I18nException {
        return ResponseEntity.ok(droneRegistrationService.submit(request));
    }

    @PutMapping("/{serialNumber}")
    @Operation(summary = "Update or resync an existing local drone registration state to UTM")
    public ResponseEntity<DroneRegistrationStatusDto> update(@PathVariable String serialNumber,
                                                             @Valid @RequestBody DroneRegistrationSubmitRequest request)
            throws I18nException {
        return ResponseEntity.ok(droneRegistrationService.update(serialNumber, request));
    }

    @GetMapping("/utm/{utmDroneId}")
    @Operation(summary = "Get drone registration data directly from UTM by UTM id")
    public ResponseEntity<DroneRegistrationStatusDto> getByUtmDroneId(@PathVariable String utmDroneId) throws I18nException {
        return ResponseEntity.ok(droneRegistrationService.getByUtmDroneId(utmDroneId));
    }

    @GetMapping("/utm")
    @Operation(summary = "Search drone registration data directly from UTM by business keys")
    public ResponseEntity<List<DroneRegistrationStatusDto>> searchUtm(@RequestParam(required = false) String serialNumber,
                                                                      @RequestParam(required = false) String registrationId)
            throws I18nException {
        return ResponseEntity.ok(droneRegistrationService.searchUtm(serialNumber, registrationId));
    }

    @GetMapping("/{serialNumber}")
    @Operation(summary = "Get current local drone registration sync state by local serial number")
    public ResponseEntity<DroneRegistrationStatusDto> getBySerialNumber(@PathVariable String serialNumber) throws I18nException {
        return ResponseEntity.ok(droneRegistrationService.getBySerialNumber(serialNumber));
    }
}
