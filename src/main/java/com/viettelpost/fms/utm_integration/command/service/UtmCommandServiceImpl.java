package com.viettelpost.fms.utm_integration.command.service;

import com.viettelpost.fms.utm_integration.command.domain.CommandStatus;
import com.viettelpost.fms.utm_integration.command.domain.UtmCommandEntity;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandMessage;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;
import com.viettelpost.fms.utm_integration.command.repository.UtmCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UtmCommandServiceImpl implements UtmCommandService {

    private final UtmCommandRepository utmCommandRepository;
    private final UtmCommandAckService utmCommandAckService;

    @Override
    @Transactional
    public UtmCommandStatusDto receive(UtmCommandMessage message) {
        return utmCommandRepository.findByCommandId(message.getCommandId())
                .map(this::toDto)
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
