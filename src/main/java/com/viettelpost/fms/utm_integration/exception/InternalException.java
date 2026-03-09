package com.viettelpost.fms.utm_integration.exception;

import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;

import java.util.Collections;
import java.util.List;

public class InternalException extends I18nException {

    public InternalException(ErrorCode errorCode) {
        this(null, errorCode.name(), errorCode.getI18Key(), Collections.emptyList());
    }

    public InternalException(ErrorCode errorCode, List<String> args) {
        super(null, errorCode.name(), errorCode.getI18Key(), args == null ? Collections.emptyList() : List.of(args));
    }

    public InternalException(String fields, String errorCode, String key, List<Object> args) {
        super(fields, errorCode, key, args);
    }
}
