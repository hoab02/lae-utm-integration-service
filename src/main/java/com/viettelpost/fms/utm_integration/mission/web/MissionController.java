package com.viettelpost.fms.utm_integration.mission.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.mission.dto.MissionEmergencyRequest;
import com.viettelpost.fms.utm_integration.mission.dto.MissionEventRequest;
import com.viettelpost.fms.utm_integration.mission.dto.MissionStatusDto;
import com.viettelpost.fms.utm_integration.mission.service.MissionService;
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
@RequestMapping("/internal/utm/missions")
@Tag(name = "Mission Runtime")
public class MissionController {

    private final MissionService missionService;

    @PostMapping("/airborne")
    @Operation(summary = "Report mission airborne state to UTM and persist the runtime state")
    public ResponseEntity<MissionStatusDto> reportAirborne(@Valid @RequestBody MissionEventRequest request)
            throws I18nException {
        return ResponseEntity.ok(missionService.reportAirborne(request));
    }

    @PostMapping("/landing")
    @Operation(summary = "Report mission landing state to UTM and persist the runtime state")
    public ResponseEntity<MissionStatusDto> reportLanding(@Valid @RequestBody MissionEventRequest request)
            throws I18nException {
        return ResponseEntity.ok(missionService.reportLanding(request));
    }

    @PostMapping("/complete")
    @Operation(summary = "Report mission completion to UTM and persist the runtime state")
    public ResponseEntity<MissionStatusDto> reportCompletion(@Valid @RequestBody MissionEventRequest request)
            throws I18nException {
        return ResponseEntity.ok(missionService.reportCompletion(request));
    }

    @PostMapping("/emergency")
    @Operation(summary = "Report mission emergency state to UTM and persist the runtime state")
    public ResponseEntity<MissionStatusDto> reportEmergency(@Valid @RequestBody MissionEmergencyRequest request)
            throws I18nException {
        return ResponseEntity.ok(missionService.reportEmergency(request));
    }
}