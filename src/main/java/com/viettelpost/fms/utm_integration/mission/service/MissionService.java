package com.viettelpost.fms.utm_integration.mission.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.mission.dto.MissionEmergencyRequest;
import com.viettelpost.fms.utm_integration.mission.dto.MissionEventRequest;
import com.viettelpost.fms.utm_integration.mission.dto.MissionStatusDto;

public interface MissionService {

    MissionStatusDto reportAirborne(MissionEventRequest request) throws I18nException;

    MissionStatusDto reportLanding(MissionEventRequest request) throws I18nException;

    MissionStatusDto reportCompletion(MissionEventRequest request) throws I18nException;

    MissionStatusDto reportEmergency(MissionEmergencyRequest request) throws I18nException;
}