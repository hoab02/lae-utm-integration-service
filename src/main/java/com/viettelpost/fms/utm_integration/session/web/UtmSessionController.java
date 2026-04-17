package com.viettelpost.fms.utm_integration.session.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionStatusDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/utm/session")
@RequiredArgsConstructor
public class UtmSessionController implements UtmSessionResource {

    private final UtmSessionService utmSessionService;

    @PostMapping("/connect")
    @Override
    public ResponseEntity<UtmSessionStatusDto> connect() throws I18nException {
        return ResponseEntity.ok(utmSessionService.connect());
    }

    @PostMapping("/refresh")
    @Override
    public ResponseEntity<UtmSessionStatusDto> refreshIfNeeded() {
        return ResponseEntity.ok(utmSessionService.refreshIfNeeded());
    }

    @PostMapping("/disconnect")
    @Override
    public ResponseEntity<UtmSessionStatusDto> disconnect() throws I18nException {
        return ResponseEntity.ok(utmSessionService.disconnect());
    }

    @GetMapping("/status")
    @Override
    public ResponseEntity<UtmSessionStatusDto> getStatus() {
        return ResponseEntity.ok(utmSessionService.getCurrentStatus());
    }
}