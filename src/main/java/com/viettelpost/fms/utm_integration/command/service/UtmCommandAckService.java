package com.viettelpost.fms.utm_integration.command.service;

import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;

public interface UtmCommandAckService {

    void prepareAck(UtmCommandStatusDto command);
}
