package com.viettelpost.fms.utm_integration.session.client;

import com.viettelpost.fms.utm_integration.session.dto.request.UtmRefreshTokenRequest;
import com.viettelpost.fms.utm_integration.session.dto.request.UtmTokenRequest;
import com.viettelpost.fms.utm_integration.session.dto.response.UtmTokenResponse;

public interface UtmAuthClient {

    UtmTokenResponse requestToken(UtmTokenRequest request);

    UtmTokenResponse refreshToken(UtmRefreshTokenRequest request);
}
