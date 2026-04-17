package com.viettelpost.fms.utm_integration.approval.client;

import com.viettelpost.fms.utm_integration.approval.config.ApprovalProperties;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitRequest;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UtmFlightApprovalClientImpl implements UtmFlightApprovalClient {

    private final ApprovalProperties approvalProperties;

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

        return restClient.post()
                .uri(approvalProperties.getSubmitPath())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(UtmFlightApprovalSubmitResponse.class);
    }
}