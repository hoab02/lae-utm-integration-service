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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/utm/drones")
@Tag(name = "Drone Registration")
public class DroneRegistrationController {

    private final DroneRegistrationService droneRegistrationService;

    @PostMapping
    @Operation(summary = "Submit drone registration to UTM and persist the current state")
    public ResponseEntity<DroneRegistrationStatusDto> submit(@Valid @RequestBody DroneRegistrationSubmitRequest request)
            throws I18nException {
        return ResponseEntity.ok(droneRegistrationService.submit(request));
    }

    @GetMapping("/{droneId}")
    @Operation(summary = "Get current drone registration state by drone id")
    public ResponseEntity<DroneRegistrationStatusDto> getByDroneId(@PathVariable String droneId) throws I18nException {
        return ResponseEntity.ok(droneRegistrationService.getByDroneId(droneId));
    }
}
