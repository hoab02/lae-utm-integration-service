package com.viettelpost.fms.utm_integration.command.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandMessage;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;

public interface UtmCommandService {

    UtmCommandStatusDto receive(UtmCommandMessage message) throws I18nException;
}
