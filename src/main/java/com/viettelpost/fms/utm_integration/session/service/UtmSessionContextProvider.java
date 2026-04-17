package com.viettelpost.fms.utm_integration.session.service;

import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;

public interface UtmSessionContextProvider {

    UtmSessionContextDto getRequiredSessionContext() throws InternalException;
}
