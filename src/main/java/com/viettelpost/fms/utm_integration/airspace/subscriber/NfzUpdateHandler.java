package com.viettelpost.fms.utm_integration.airspace.subscriber;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateMessage;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;
import com.viettelpost.fms.utm_integration.airspace.service.AirspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NfzUpdateHandler {

    private final AirspaceService airspaceService;

    public AirspaceUpdateStatusDto receive(AirspaceUpdateMessage message) throws I18nException {
        return airspaceService.receive(message.toBuilder()
                .type(AirspaceUpdateType.NFZ)
                .build());
    }
}
