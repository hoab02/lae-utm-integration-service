package com.viettelpost.fms.utm_integration.session.web;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionStatusDto;
import org.springframework.http.ResponseEntity;

public interface UtmSessionResource {

    ResponseEntity<UtmSessionStatusDto> connect() throws I18nException;

    ResponseEntity<UtmSessionStatusDto> refreshIfNeeded();

    ResponseEntity<UtmSessionStatusDto> disconnect() throws I18nException;

    ResponseEntity<UtmSessionStatusDto> getStatus();
}