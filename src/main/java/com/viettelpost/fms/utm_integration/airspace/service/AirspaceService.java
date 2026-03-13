package com.viettelpost.fms.utm_integration.airspace.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateMessage;
import com.viettelpost.fms.utm_integration.airspace.dto.AirspaceUpdateStatusDto;

import java.util.Optional;

public interface AirspaceService {

    AirspaceUpdateStatusDto receive(AirspaceUpdateMessage message) throws I18nException;

    Optional<AirspaceUpdateStatusDto> getLatest(AirspaceUpdateType type);
}
