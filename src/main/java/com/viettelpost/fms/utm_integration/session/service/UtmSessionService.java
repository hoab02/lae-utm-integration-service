package com.viettelpost.fms.utm_integration.session.service;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionStatusDto;

public interface UtmSessionService {

    UtmSessionStatusDto connect() throws I18nException;

    UtmSessionStatusDto disconnect() throws I18nException;

    UtmSessionStatusDto getCurrentStatus();

    UtmSessionContextDto getCurrentSessionContext();
}