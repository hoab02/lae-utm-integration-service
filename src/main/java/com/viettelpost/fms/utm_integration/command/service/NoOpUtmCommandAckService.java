package com.viettelpost.fms.utm_integration.command.service;

import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;
import org.springframework.stereotype.Service;

@Service
public class NoOpUtmCommandAckService implements UtmCommandAckService {

    @Override
    public void prepareAck(UtmCommandStatusDto command) {
        // Placeholder for phase 5 command acknowledgement direction.
    }
}
