package com.viettelpost.fms.utm_integration.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ERROR_GENERAL("error.general"),
    ERROR_REQUEST_INVALID("error.request.invalid"),
    ERROR_PERMISSION_DENIED("error.permission"),
    ERROR_SESSION_ALREADY_CONNECTED("error.session.already.connected"),
    ERROR_SESSION_NOT_CONNECTED("error.session.not.connected"),
    ERROR_APPROVAL_NOT_FOUND("error.approval.not.found");

    private final String i18Key;
}