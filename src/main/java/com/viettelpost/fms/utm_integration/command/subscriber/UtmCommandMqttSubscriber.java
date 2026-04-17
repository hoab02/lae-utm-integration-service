package com.viettelpost.fms.utm_integration.command.subscriber;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandMessage;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;
import com.viettelpost.fms.utm_integration.command.service.UtmCommandService;
import com.viettelpost.fms.utm_integration.reliability.inbox.InboxMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UtmCommandMqttSubscriber {

    private static final String UTM_COMMAND_CHANNEL = "UTM_COMMAND";

    private final UtmCommandService utmCommandService;
    private final InboxMessageService inboxMessageService;

    public UtmCommandStatusDto receive(UtmCommandMessage message) throws I18nException {
        log.info("utm_command_receive_start commandId={} missionId={} commandType={}",
                message.getCommandId(), message.getMissionId(), message.getCommandType());
        boolean firstSeen = inboxMessageService.firstSeen(UTM_COMMAND_CHANNEL, message.getCommandId());
        UtmCommandStatusDto status = utmCommandService.receive(message);
        if (firstSeen) {
            inboxMessageService.markProcessed(UTM_COMMAND_CHANNEL, message.getCommandId());
            log.info("utm_command_receive_processed commandId={} missionId={} status={}",
                    status.getCommandId(), status.getMissionId(), status.getStatus());
        } else {
            log.info("utm_command_receive_duplicate commandId={} missionId={} status={}",
                    status.getCommandId(), status.getMissionId(), status.getStatus());
        }
        return status;
    }
}