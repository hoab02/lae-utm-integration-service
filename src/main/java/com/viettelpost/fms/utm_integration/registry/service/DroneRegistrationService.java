package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationSubmitRequest;

public interface DroneRegistrationService {

    DroneRegistrationStatusDto submit(DroneRegistrationSubmitRequest request) throws I18nException;

    DroneRegistrationStatusDto getByDroneId(String droneId) throws I18nException;
}
