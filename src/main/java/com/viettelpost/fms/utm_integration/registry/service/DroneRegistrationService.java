package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationSubmitRequest;

import java.util.List;

public interface DroneRegistrationService {

    DroneRegistrationStatusDto submit(DroneRegistrationSubmitRequest request) throws I18nException;

    DroneRegistrationStatusDto update(String serialNumber, DroneRegistrationSubmitRequest request) throws I18nException;

    DroneRegistrationStatusDto getBySerialNumber(String serialNumber) throws I18nException;

    DroneRegistrationStatusDto getByUtmDroneId(String utmDroneId) throws I18nException;

    List<DroneRegistrationStatusDto> searchUtm(String serialNumber, String registrationId) throws I18nException;
}
