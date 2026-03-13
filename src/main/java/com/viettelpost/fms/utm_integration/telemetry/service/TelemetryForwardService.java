package com.viettelpost.fms.utm_integration.telemetry.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;

public interface TelemetryForwardService {

    void forward(TelemetryMessage message) throws I18nException;
}
