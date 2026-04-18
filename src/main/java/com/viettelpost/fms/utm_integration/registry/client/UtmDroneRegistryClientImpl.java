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
public class UtmDroneRegistryClientImpl implements UtmDroneRegistryClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${integration.utm.base-url}")
    private String utmBaseUrl;

    @Value("${integration.utm.registry.drone-path:/DroneRegistry}")
    private String dronePath;

    @Override
    public DroneRegistryRecord create(String accessToken, DroneRegistryUpsertRequest request) throws InternalException {
        URI uri = buildBaseUri();
        try {
            log.info("utm_drone_registry_create_start url={} registrationId={} serialNumber={}",
                    uri,
                    request.basicInfo() != null ? request.basicInfo().registrationId() : null,
                    request.basicInfo() != null ? request.basicInfo().serialNumber() : null);

            log.info("utm_drone_registry_create_payload={}", objectMapper.writeValueAsString(request));
        } catch (Exception ex) {
            log.warn("utm_drone_registry_create_payload_serialize_failed message={}", ex.getMessage(), ex);
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
    public DroneRegistryRecord getById(String accessToken, String utmDroneId) throws InternalException {
        URI uri = buildByIdUri(utmDroneId);
        log.info("utm_drone_registry_get_start url={} utmDroneId={}", uri, utmDroneId);

        String body = restClient.get()
                .uri(uri)
                .headers(h -> applyBearerAuth(h, accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        return parseSingleRecord(body);
    }

    @Override
    public List<DroneRegistryRecord> search(String accessToken, DroneRegistrySearchRequest request) throws InternalException {
        URI uri = buildSearchUri(request);
        log.info("utm_drone_registry_search_start url={} registrationId={} serialNumber={}",
                uri, request.registrationId(), request.serialNumber());

        String body = restClient.get()
                .uri(uri)
                .headers(h -> applyBearerAuth(h, accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        return parseSearchRecords(body);
    }

    @Override
    public DroneRegistryRecord update(String accessToken, String utmDroneId, DroneRegistryUpsertRequest request) throws InternalException {
        URI uri = buildByIdUri(utmDroneId);
        log.info("utm_drone_registry_update_start url={} utmDroneId={}", uri, utmDroneId);

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
                .path(normalizePath(dronePath))
                .build(true)
                .toUri();
    }

    private URI buildByIdUri(String utmDroneId) {
        return UriComponentsBuilder.fromUriString(utmBaseUrl)
                .path(normalizePath(dronePath))
                .pathSegment(utmDroneId)
                .build(true)
                .toUri();
    }

    private URI buildSearchUri(DroneRegistrySearchRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(utmBaseUrl)
                .path(normalizePath(dronePath));

        if (StringUtils.hasText(request.serialNumber())) {
            builder.queryParam("serial_number", request.serialNumber());
        }
        if (StringUtils.hasText(request.registrationId())) {
            builder.queryParam("registration_id", request.registrationId());
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

    private DroneRegistryRecord parseSingleRecord(String body) throws InternalException {
        try {
            JavaType envelopeType = objectMapper.getTypeFactory()
                    .constructParametricType(UtmRegistryResponseEnvelope.class, DroneRegistryRecord.class);

            UtmRegistryResponseEnvelope<DroneRegistryRecord> envelope =
                    objectMapper.readValue(body, envelopeType);

            validateEnvelope(envelope, "UTM drone registry error");
            return envelope.getData();
        } catch (InternalException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse drone registry single response", ex);
        }
    }

    private List<DroneRegistryRecord> parseSearchRecords(String body) throws InternalException {
        try {
            JavaType envelopeType = objectMapper.getTypeFactory()
                    .constructParametricType(UtmRegistryResponseEnvelope.class, DroneRegistrySearchData.class);

            UtmRegistryResponseEnvelope<DroneRegistrySearchData> envelope =
                    objectMapper.readValue(body, envelopeType);

            validateEnvelope(envelope, "UTM drone registry search error");

            if (envelope.getData() == null || envelope.getData().items() == null) {
                return Collections.emptyList();
            }

            return envelope.getData().items();
        } catch (InternalException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse drone registry search response", ex);
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