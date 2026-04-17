package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Value("${integration.utm.registry.drone-path:/drones}")
    private String dronePath;

    @Override
    public DroneRegistryRecord create(String accessToken, DroneRegistryUpsertRequest request) {
        URI uri = buildBaseUri();
        log.info("utm_drone_registry_create_start url={} registrationId={} serialNumber={}", uri, request.basicInfo() != null ? request.basicInfo().registrationId() : null, request.basicInfo() != null ? request.basicInfo().serialNumber() : null);
        return parseSingleRecord(restClient.post().uri(uri).headers(h -> applyBearerAuth(h, accessToken)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(request).retrieve().body(String.class));
    }

    @Override
    public DroneRegistryRecord getById(String accessToken, String utmDroneId) {
        URI uri = buildByIdUri(utmDroneId);
        log.info("utm_drone_registry_get_start url={} utmDroneId={}", uri, utmDroneId);
        return parseSingleRecord(restClient.get().uri(uri).headers(h -> applyBearerAuth(h, accessToken)).accept(MediaType.APPLICATION_JSON).retrieve().body(String.class));
    }

    @Override
    public List<DroneRegistryRecord> search(String accessToken, DroneRegistrySearchRequest request) {
        URI uri = buildSearchUri(request);
        log.info("utm_drone_registry_search_start url={} registrationId={} serialNumber={}", uri, request.registrationId(), request.serialNumber());
        return parseRecordList(restClient.get().uri(uri).headers(h -> applyBearerAuth(h, accessToken)).accept(MediaType.APPLICATION_JSON).retrieve().body(String.class));
    }

    @Override
    public DroneRegistryRecord update(String accessToken, String utmDroneId, DroneRegistryUpsertRequest request) {
        URI uri = buildByIdUri(utmDroneId);
        log.info("utm_drone_registry_update_start url={} utmDroneId={}", uri, utmDroneId);
        return parseSingleRecord(restClient.put().uri(uri).headers(h -> applyBearerAuth(h, accessToken)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(request).retrieve().body(String.class));
    }

    private URI buildBaseUri() { return UriComponentsBuilder.fromUriString(utmBaseUrl).path(normalizePath(dronePath)).build(true).toUri(); }
    private URI buildByIdUri(String utmDroneId) { return UriComponentsBuilder.fromUriString(utmBaseUrl).path(normalizePath(dronePath)).pathSegment(utmDroneId).build(true).toUri(); }
    private URI buildSearchUri(DroneRegistrySearchRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(utmBaseUrl).path(normalizePath(dronePath));
        if (StringUtils.hasText(request.serialNumber())) builder.queryParam("serial_number", request.serialNumber());
        if (StringUtils.hasText(request.registrationId())) builder.queryParam("registration_id", request.registrationId());
        return builder.build(true).toUri();
    }
    private String normalizePath(String path) { if (!StringUtils.hasText(path)) return ""; return path.startsWith("/") ? path : "/" + path; }
    private void applyBearerAuth(HttpHeaders headers, String accessToken) { if (StringUtils.hasText(accessToken)) headers.setBearerAuth(accessToken); }
    private DroneRegistryRecord parseSingleRecord(String body) { List<DroneRegistryRecord> records = parseRecordList(body); return records.isEmpty() ? null : records.get(0); }
    private List<DroneRegistryRecord> parseRecordList(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode payload = root.has("data") ? root.get("data") : root;
            if (payload == null || payload.isNull() || payload.isMissingNode()) return Collections.emptyList();
            if (payload.isArray()) return objectMapper.readerForListOf(DroneRegistryRecord.class).readValue(payload);
            return List.of(objectMapper.treeToValue(payload, DroneRegistryRecord.class));
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse drone registry response", ex);
        }
    }
}
