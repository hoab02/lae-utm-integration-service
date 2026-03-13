package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationSubmitRequest;

public interface PilotRegistrationService {

    PilotRegistrationStatusDto submit(PilotRegistrationSubmitRequest request) throws I18nException;

    PilotRegistrationStatusDto getByPilotId(String pilotId) throws I18nException;
}
