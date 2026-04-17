package com.viettelpost.fms.utm_integration.approval.service;

import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionContextProvider;
import com.viettelpost.fms.utm_integration.session.service.UtmTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ApprovalTokenProviderImpl implements ApprovalTokenProvider {

    private final UtmSessionContextProvider utmSessionContextProvider;
    private final UtmTokenManager utmTokenManager;

    @Override
    public String getAuthorizationHeaderValue() {
        try {
            UtmSessionContextDto sessionContext = utmSessionContextProvider.getRequiredSessionContext();
            String accessToken = utmTokenManager.getValidAccessToken();

            String tokenType = StringUtils.hasText(sessionContext.tokenType())
                    ? sessionContext.tokenType()
                    : "Bearer";

            return tokenType + " " + accessToken;
        } catch (InternalException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "UTM session is not ready: " + e.getMessage(),
                    e
            );
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "UTM access token is unavailable: " + e.getMessage(),
                    e
            );
        }
    }
}