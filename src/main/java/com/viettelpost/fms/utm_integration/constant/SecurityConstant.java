package com.viettelpost.fms.utm_integration.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstant {

    public static final String ACCESS_TOKEN_FORMAT = "Bearer %s";

    public static final String HEADER_FORWARDED_ADDRESS = "X-Forwarded-For";
}
