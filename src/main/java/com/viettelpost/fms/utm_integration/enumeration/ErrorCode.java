package com.viettelpost.fms.utm_integration.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ERROR_GENERAL("error.general"),
    ERROR_REQUEST_INVALID("error.request.invalid"),
    ERROR_PERMISSION_DENIED("error.permission");

    private final String i18Key;
}
