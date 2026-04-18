package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UtmPilotRegistryClientImpl implements UtmPilotRegistryClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${integration.utm.base-url}")
    private String utmBaseUrl;

    @Value("${integration.utm.registry.pilot-license-path:/PilotLicense}")
    private String pilotPath;

    @Override
    public PilotRegistryRecord create(String accessToken, PilotRegistryUpsertRequest request) throws InternalException {
        URI uri = buildBaseUri();
        log.info("utm_pilot_registry_create_start url={} licenseNumber={} personalIdNumber={}",
                uri, request.licenseNumber(), request.personalIdNumber());

        try {
            log.info("utm_pilot_registry_create_payload={}", objectMapper.writeValueAsString(request));
        } catch (Exception ex) {
            log.warn("utm_pilot_registry_create_payload_serialize_failed message={}", ex.getMessage(), ex);
        }

        String body = restClient.post()
                .uri(uri)
                .headers(h -> applyBearerAuth(h, accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        return parseSingleRecord(body);
    }

    @Override
    public PilotRegistryRecord getById(String accessToken, String utmPilotId) throws InternalException {
        URI uri = buildByIdUri(utmPilotId);
        log.info("utm_pilot_registry_get_start url={} utmPilotId={}", uri, utmPilotId);

        String body = restClient.get()
                .uri(uri)
                .headers(h -> applyBearerAuth(h, accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        return parseSingleRecord(body);
    }

    @Override
    public List<PilotRegistryRecord> search(String accessToken, PilotRegistrySearchRequest request) throws InternalException {
        URI uri = buildSearchUri(request);
        log.info("utm_pilot_registry_search_start url={} personalIdNumber={} licenseNumber={} phoneNumber={}",
                uri, request.personalIdNumber(), request.licenseNumber(), request.phoneNumber());

        String body = restClient.get()
                .uri(uri)
                .headers(h -> applyBearerAuth(h, accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        return parseSearchRecords(body);
    }

    @Override
    public PilotRegistryRecord update(String accessToken, String utmPilotId, PilotRegistryUpsertRequest request) throws InternalException {
        URI uri = buildByIdUri(utmPilotId);
        log.info("utm_pilot_registry_update_start url={} utmPilotId={}", uri, utmPilotId);

        try {
            log.info("utm_pilot_registry_update_payload={}", objectMapper.writeValueAsString(request));
        } catch (Exception ex) {
            log.warn("utm_pilot_registry_update_payload_serialize_failed message={}", ex.getMessage(), ex);
        }

        String body = restClient.put()
                .uri(uri)
                .headers(h -> applyBearerAuth(h, accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        return parseSingleRecord(body);
    }

    private URI buildBaseUri() {
        return UriComponentsBuilder.fromUriString(utmBaseUrl)
                .path(normalizePath(pilotPath))
                .build(true)
                .toUri();
    }

    private URI buildByIdUri(String utmPilotId) {
        return UriComponentsBuilder.fromUriString(utmBaseUrl)
                .path(normalizePath(pilotPath))
                .pathSegment(utmPilotId)
                .build(true)
                .toUri();
    }

    private URI buildSearchUri(PilotRegistrySearchRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(utmBaseUrl)
                .path(normalizePath(pilotPath));

        if (StringUtils.hasText(request.personalIdNumber())) {
            builder.queryParam("personal_id_number", request.personalIdNumber());
        }
        if (StringUtils.hasText(request.licenseNumber())) {
            builder.queryParam("license_number", request.licenseNumber());
        }
        if (StringUtils.hasText(request.phoneNumber())) {
            builder.queryParam("phone_number", request.phoneNumber());
        }

        return builder.build(true).toUri();
    }

    private String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private void applyBearerAuth(HttpHeaders headers, String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            headers.setBearerAuth(accessToken);
        }
    }

    private PilotRegistryRecord parseSingleRecord(String body) throws InternalException {
        try {
            JavaType envelopeType = objectMapper.getTypeFactory()
                    .constructParametricType(UtmRegistryResponseEnvelope.class, PilotRegistryRecord.class);

            UtmRegistryResponseEnvelope<PilotRegistryRecord> envelope =
                    objectMapper.readValue(body, envelopeType);

            validateEnvelope(envelope, "UTM pilot registry error");
            return envelope.getData();
        } catch (InternalException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse pilot registry single response", ex);
        }
    }

    private List<PilotRegistryRecord> parseSearchRecords(String body) throws InternalException {
        try {
            JavaType envelopeType = objectMapper.getTypeFactory()
                    .constructParametricType(UtmRegistryResponseEnvelope.class, PilotRegistrySearchData.class);

            UtmRegistryResponseEnvelope<PilotRegistrySearchData> envelope =
                    objectMapper.readValue(body, envelopeType);

            validateEnvelope(envelope, "UTM pilot registry search error");

            if (envelope.getData() == null || envelope.getData().items() == null) {
                return Collections.emptyList();
            }

            return envelope.getData().items();
        } catch (InternalException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse pilot registry search response", ex);
        }
    }

    private void validateEnvelope(UtmRegistryResponseEnvelope<?> envelope, String defaultMessage) throws InternalException {
        if (envelope == null || envelope.getCode() == null || envelope.getCode() != 0) {
            String message = envelope != null ? envelope.getMessage() : null;
            throw new InternalException(
                    null,
                    ErrorCode.ERROR_GENERAL.name(),
                    StringUtils.hasText(message) ? message : defaultMessage,
                    Collections.emptyList()
            );
        }
    }
}