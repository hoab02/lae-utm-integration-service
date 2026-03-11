package com.viettelpost.fms.utm_integration.session.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionStatusDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/utm/session")
@Tag(name = "UTM Session")
public class UtmSessionController {

    private final UtmSessionService utmSessionService;

    @PostMapping("/connect")
    @Operation(summary = "Connect to UTM and persist the current session state")
    public ResponseEntity<UtmSessionStatusDto> connect() throws I18nException {
        return ResponseEntity.ok(utmSessionService.connect());
    }

    @PostMapping("/disconnect")
    @Operation(summary = "Disconnect the current UTM session and persist the state")
    public ResponseEntity<UtmSessionStatusDto> disconnect() throws I18nException {
        return ResponseEntity.ok(utmSessionService.disconnect());
    }

    @GetMapping("/status")
    @Operation(summary = "Get the current UTM session status")
    public ResponseEntity<UtmSessionStatusDto> getStatus() {
        return ResponseEntity.ok(utmSessionService.getCurrentStatus());
    }
}
