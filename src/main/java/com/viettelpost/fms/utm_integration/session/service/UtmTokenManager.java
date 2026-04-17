package com.viettelpost.fms.utm_integration.session.service;

import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;

public interface UtmTokenManager {

    UtmSessionEntity connect();

    UtmSessionEntity refreshIfNeeded();

    String getValidAccessToken();

    UtmSessionContextDto getCurrentSessionContext();

    UtmSessionEntity disconnect();
}
