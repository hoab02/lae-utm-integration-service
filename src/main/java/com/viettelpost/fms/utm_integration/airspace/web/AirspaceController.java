package com.viettelpost.fms.utm_integration.airspace.web;

import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;
import com.viettelpost.fms.utm_integration.airspace.service.AirspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/utm/airspace")
@Tag(name = "Airspace")
public class AirspaceController {

    private final AirspaceService airspaceService;

    @GetMapping("/latest/nfz")
    @Operation(summary = "Get the latest NFZ airspace update")
    public ResponseEntity<AirspaceUpdateStatusDto> getLatestNfz() {
        return airspaceService.getLatest(AirspaceUpdateType.NFZ)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/latest/corridor")
    @Operation(summary = "Get the latest corridor airspace update")
    public ResponseEntity<AirspaceUpdateStatusDto> getLatestCorridor() {
        return airspaceService.getLatest(AirspaceUpdateType.CORRIDOR)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
