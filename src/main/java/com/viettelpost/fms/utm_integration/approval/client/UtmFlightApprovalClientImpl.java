package com.viettelpost.fms.utm_integration.approval.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.approval.config.ApprovalProperties;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitRequest;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitResponse;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitWrapperResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class UtmFlightApprovalClientImpl implements UtmFlightApprovalClient {

    private final ApprovalProperties approvalProperties;
    private final ObjectMapper objectMapper;

    @Value("${integration.utm.base-url}")
    private String utmBaseUrl;

    @Override
    public UtmFlightApprovalSubmitResponse submit(
            UtmFlightApprovalSubmitRequest request,
            String authorizationHeaderValue
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        RestClient restClient = RestClient.builder()
                .baseUrl(utmBaseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();

        String requestJson = toJson(request);
        String maskedAuthorization = maskAuthorizationHeader(authorizationHeaderValue);

        log.info("utm_flight_approval_submit_request_start baseUrl={} path={} authorization={} payload={}",
                utmBaseUrl,
                approvalProperties.getSubmitPath(),
                maskedAuthorization,
                requestJson);

        try {
            UtmFlightApprovalSubmitWrapperResponse wrapperResponse = restClient.post()
                    .uri(approvalProperties.getSubmitPath())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(UtmFlightApprovalSubmitWrapperResponse.class);

            log.info("utm_flight_approval_submit_request_success baseUrl={} path={} response={}",
                    utmBaseUrl,
                    approvalProperties.getSubmitPath(),
                    toJson(wrapperResponse));

            if (wrapperResponse == null) {
                throw new IllegalStateException("UTM approval submit response is null");
            }

            if (wrapperResponse.getCode() == null || wrapperResponse.getCode() != 0) {
                throw new IllegalStateException(
                        "UTM approval submit failed, code=" + wrapperResponse.getCode()
                                + ", message=" + wrapperResponse.getMessage()
                );
            }

            if (wrapperResponse.getData() == null) {
                throw new IllegalStateException("UTM approval submit response data is null");
            }

            return wrapperResponse.getData();
        } catch (Exception ex) {
            log.error("utm_flight_approval_submit_request_failed baseUrl={} path={} authorization={} payload={}",
                    utmBaseUrl,
                    approvalProperties.getSubmitPath(),
                    maskedAuthorization,
                    requestJson,
                    ex);
            throw ex;
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("utm_flight_approval_json_serialize_failed type={}",
                    value != null ? value.getClass().getSimpleName() : "null", e);
            return "{\"error\":\"json_serialize_failed\"}";
        }
    }

    private String maskAuthorizationHeader(String authorizationHeaderValue) {
        if (authorizationHeaderValue == null || authorizationHeaderValue.isBlank()) {
            return "blank";
        }

        int firstSpace = authorizationHeaderValue.indexOf(' ');
        if (firstSpace < 0) {
            return "***";
        }

        String scheme = authorizationHeaderValue.substring(0, firstSpace);
        String token = authorizationHeaderValue.substring(firstSpace + 1).trim();

        if (token.length() <= 10) {
            return scheme + " ***";
        }

        return scheme + " " + token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}