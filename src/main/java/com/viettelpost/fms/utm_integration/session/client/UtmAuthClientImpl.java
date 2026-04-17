package com.viettelpost.fms.utm_integration.session.client;

import com.viettelpost.fms.utm_integration.session.config.UtmSessionProperties;
import com.viettelpost.fms.utm_integration.session.dto.request.UtmRefreshTokenRequest;
import com.viettelpost.fms.utm_integration.session.dto.request.UtmTokenRequest;
import com.viettelpost.fms.utm_integration.session.dto.response.UtmTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class UtmAuthClientImpl implements UtmAuthClient {

    private final RestClient restClient;
    private final UtmSessionProperties sessionProperties;

    @Override
    public UtmTokenResponse requestToken(UtmTokenRequest request) {
        log.info("utm_auth_token_request_start url={}", sessionProperties.getBaseUrl() + sessionProperties.getAuth().getTokenPath());
        return restClient.post()
                .uri(sessionProperties.getBaseUrl() + sessionProperties.getAuth().getTokenPath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(toTokenForm(request))
                .retrieve()
                .body(UtmTokenResponse.class);
    }

    @Override
    public UtmTokenResponse refreshToken(UtmRefreshTokenRequest request) {
        log.info("utm_auth_refresh_request_start url={}", sessionProperties.getBaseUrl() + sessionProperties.getAuth().getRefreshPath());
        return restClient.post()
                .uri(sessionProperties.getBaseUrl() + sessionProperties.getAuth().getRefreshPath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(toRefreshForm(request))
                .retrieve()
                .body(UtmTokenResponse.class);
    }

    private MultiValueMap<String, String> toTokenForm(UtmTokenRequest request) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", request.grantType());
        form.add("username", request.username());
        form.add("password", request.password());
        return form;
    }

    private MultiValueMap<String, String> toRefreshForm(UtmRefreshTokenRequest request) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", request.grantType());
        form.add("refresh_token", request.refreshToken());
        return form;
    }
}
