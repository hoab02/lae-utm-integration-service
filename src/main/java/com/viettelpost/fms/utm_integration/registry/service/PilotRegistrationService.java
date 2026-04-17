package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationSubmitRequest;

import java.util.List;

public interface PilotRegistrationService {

    PilotRegistrationStatusDto submit(PilotRegistrationSubmitRequest request) throws I18nException;

    PilotRegistrationStatusDto update(String personalIdNumber, PilotRegistrationSubmitRequest request) throws I18nException;

    PilotRegistrationStatusDto getByPersonalIdNumber(String personalIdNumber) throws I18nException;

    PilotRegistrationStatusDto getByUtmPilotId(String utmPilotId) throws I18nException;

    List<PilotRegistrationStatusDto> searchUtm(String personalIdNumber, String licenseNumber, String phoneNumber) throws I18nException;
}
