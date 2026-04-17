package com.viettelpost.fms.utm_integration.command.service;

import com.viettelpost.fms.utm_integration.command.domain.CommandStatus;
import com.viettelpost.fms.utm_integration.command.domain.UtmCommandEntity;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandMessage;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;
import com.viettelpost.fms.utm_integration.command.repository.UtmCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtmCommandServiceImpl implements UtmCommandService {

    private final UtmCommandRepository utmCommandRepository;
    private final UtmCommandAckService utmCommandAckService;

    @Override
    @Transactional
    public UtmCommandStatusDto receive(UtmCommandMessage message) {
        log.info("command_receive_start commandId={} missionId={} commandType={}",
                message.getCommandId(), message.getMissionId(), message.getCommandType());
        return utmCommandRepository.findByCommandId(message.getCommandId())
                .map(existing -> {
                    log.info("command_receive_duplicate commandId={} missionId={} status={}",
                            existing.getCommandId(), existing.getMissionId(), existing.getStatus());
                    return toDto(existing);
                })
                .orElseGet(() -> createCommand(message));
    }

    private UtmCommandStatusDto createCommand(UtmCommandMessage message) {
        UtmCommandEntity command = UtmCommandEntity.builder()
                .commandId(message.getCommandId())
                .missionId(message.getMissionId())
                .commandType(message.getCommandType())
                .priority(message.getPriority())
                .payload(message.getPayload())
                .status(CommandStatus.RECEIVED)
                .receivedAt(message.getReceivedAt() != null ? message.getReceivedAt() : new Date())
                .build();

        UtmCommandStatusDto status = toDto(utmCommandRepository.save(command));
        log.info("command_receive_success commandId={} missionId={} status={}",
                status.getCommandId(), status.getMissionId(), status.getStatus());
        utmCommandAckService.prepareAck(status);
        return status;
    }

    private UtmCommandStatusDto toDto(UtmCommandEntity command) {
        return UtmCommandStatusDto.builder()
                .id(command.getId())
                .commandId(command.getCommandId())
                .missionId(command.getMissionId())
                .commandType(command.getCommandType())
                .priority(command.getPriority())
                .payload(command.getPayload())
                .status(command.getStatus())
                .receivedAt(command.getReceivedAt())
                .ackAt(command.getAckAt())
                .executedAt(command.getExecutedAt())
                .failureReason(command.getFailureReason())
                .build();
    }
}