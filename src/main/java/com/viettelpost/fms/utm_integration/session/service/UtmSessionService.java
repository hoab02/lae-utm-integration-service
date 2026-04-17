package com.viettelpost.fms.utm_integration.session.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionStatusDto;

public interface UtmSessionService {

    UtmSessionStatusDto connect() throws I18nException;

    UtmSessionStatusDto disconnect() throws I18nException;

    UtmSessionStatusDto refreshIfNeeded();

    UtmSessionStatusDto getCurrentStatus();

    UtmSessionContextDto getCurrentSessionContext();
}