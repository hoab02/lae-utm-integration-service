package com.viettelpost.fms.utm_integration.command.subscriber;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandMessage;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;
import com.viettelpost.fms.utm_integration.command.service.UtmCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UtmCommandMqttSubscriber {

    private final UtmCommandService utmCommandService;

    public UtmCommandStatusDto receive(UtmCommandMessage message) throws I18nException {
        return utmCommandService.receive(message);
    }
}
