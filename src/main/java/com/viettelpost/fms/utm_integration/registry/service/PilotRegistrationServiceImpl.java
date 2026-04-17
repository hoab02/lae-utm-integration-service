package com.viettelpost.fms.utm_integration.registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.client.PilotRegistryRecord;
import com.viettelpost.fms.utm_integration.registry.client.PilotRegistrySearchRequest;
import com.viettelpost.fms.utm_integration.registry.client.PilotRegistryUpsertRequest;
import com.viettelpost.fms.utm_integration.registry.client.UtmPilotRegistryClient;
import com.viettelpost.fms.utm_integration.registry.domain.PilotRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationSyncStatus;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.kafka.PilotRegistrationResultKafkaPublisher;
import com.viettelpost.fms.utm_integration.registry.repository.PilotRegistrationRepository;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionContextProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PilotRegistrationServiceImpl implements PilotRegistrationService {
    private final PilotRegistrationRepository pilotRegistrationRepository;
    private final UtmPilotRegistryClient utmPilotRegistryClient;
    private final UtmSessionContextProvider utmSessionContextProvider;
    private final PilotRegistrationMapper pilotRegistrationMapper;
    private final PilotRegistrationResultKafkaPublisher publisher;
    private final ObjectMapper objectMapper;

    @Override
    public PilotRegistrationStatusDto submit(PilotRegistrationSubmitRequest request) throws InternalException {
        PilotRegistrationEntity entity = pilotRegistrationRepository.findByPersonalIdNumber(request.getPersonalIdNumber()).orElseGet(() -> newEntity(request.getPersonalIdNumber()));
        return createAndPersist(entity, request);
    }

    @Override
    public PilotRegistrationStatusDto update(String personalIdNumber, PilotRegistrationSubmitRequest request) throws InternalException {
        PilotRegistrationEntity entity = pilotRegistrationRepository.findByPersonalIdNumber(personalIdNumber).orElseThrow(() -> new InternalException(ErrorCode.ERROR_PILOT_REGISTRATION_NOT_FOUND));
        if (entity.getUtmPilotId() == null || entity.getUtmPilotId().isBlank()) throw new InternalException(ErrorCode.ERROR_PILOT_REGISTRATION_NOT_FOUND);
        return updateAndPersist(entity, request, entity.getUtmPilotId());
    }

    @Override
    public PilotRegistrationStatusDto getByPersonalIdNumber(String personalIdNumber) throws InternalException {
        return pilotRegistrationRepository.findByPersonalIdNumber(personalIdNumber).map(pilotRegistrationMapper::toDto).orElseThrow(() -> new InternalException(ErrorCode.ERROR_PILOT_REGISTRATION_NOT_FOUND));
    }

    @Override
    public PilotRegistrationStatusDto getByUtmPilotId(String utmPilotId) throws InternalException {
        UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
        PilotRegistryRecord record = utmPilotRegistryClient.getById(session.accessToken(), utmPilotId);
        if (record == null) throw new InternalException(ErrorCode.ERROR_PILOT_REGISTRATION_NOT_FOUND);
        return pilotRegistrationMapper.toUtmDto(record);
    }

    @Override
    public List<PilotRegistrationStatusDto> searchUtm(String personalIdNumber, String licenseNumber, String phoneNumber) throws InternalException {
        UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
        return utmPilotRegistryClient.search(session.accessToken(), new PilotRegistrySearchRequest(personalIdNumber, licenseNumber, phoneNumber)).stream().map(pilotRegistrationMapper::toUtmDto).toList();
    }

    private PilotRegistrationStatusDto createAndPersist(PilotRegistrationEntity entity, PilotRegistrationSubmitRequest request) throws InternalException {
        Date now = new Date();
        PilotRegistryUpsertRequest payload = pilotRegistrationMapper.toUpsertRequest(request);
        markSyncing(entity, request, payload, now);
        try {
            UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
            PilotRegistryRecord record = utmPilotRegistryClient.create(session.accessToken(), payload);
            applySuccess(entity, record, now);
            return saveAndPublish(entity);
        } catch (InternalException ex) {
            applyFailure(entity, ex, now); saveAndPublish(entity); throw ex;
        } catch (RuntimeException ex) {
            applyFailure(entity, ex, now); saveAndPublish(entity); throw ex;
        }
    }

    private PilotRegistrationStatusDto updateAndPersist(PilotRegistrationEntity entity, PilotRegistrationSubmitRequest request, String utmPilotId) throws InternalException {
        Date now = new Date();
        PilotRegistryUpsertRequest payload = pilotRegistrationMapper.toUpsertRequest(request);
        markSyncing(entity, request, payload, now);
        try {
            UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
            PilotRegistryRecord record = utmPilotRegistryClient.update(session.accessToken(), utmPilotId, payload);
            applySuccess(entity, record, now);
            return saveAndPublish(entity);
        } catch (InternalException ex) {
            applyFailure(entity, ex, now); saveAndPublish(entity); throw ex;
        } catch (RuntimeException ex) {
            applyFailure(entity, ex, now); saveAndPublish(entity); throw ex;
        }
    }

    private PilotRegistrationEntity newEntity(String personalIdNumber) {
        PilotRegistrationEntity entity = new PilotRegistrationEntity();
        entity.setPersonalIdNumber(personalIdNumber);
        entity.setSyncStatus(RegistrationSyncStatus.PENDING);
        entity.setStatus(RegistrationStatus.DRAFT);
        return entity;
    }

    private void markSyncing(PilotRegistrationEntity entity, PilotRegistrationSubmitRequest request, Object payload, Date now) {
        pilotRegistrationMapper.applySubmitRequest(entity, request);
        entity.setSyncStatus(RegistrationSyncStatus.SYNCING);
        entity.setSyncErrorCode(null);
        entity.setSyncErrorMessage(null);
        entity.setRequestPayloadSnapshot(toJson(payload));
        entity.setSubmittedAt(now);
    }

    private void applySuccess(PilotRegistrationEntity entity, PilotRegistryRecord record, Date now) {
        pilotRegistrationMapper.applyUtmRecord(entity, record);
        entity.setSyncStatus(RegistrationSyncStatus.SYNCED);
        entity.setSyncErrorCode(null);
        entity.setSyncErrorMessage(null);
        entity.setResponsePayloadSnapshot(toJson(record));
        entity.setLastSyncedAt(now);
        entity.setApprovedAt(null);
        entity.setRejectedAt(null);
    }

    private void applyFailure(PilotRegistrationEntity entity, Exception ex, Date now) {
        log.error("pilot_registration_sync_failed personalIdNumber={} message={}", entity.getPersonalIdNumber(), ex.getMessage(), ex);
        entity.setSyncStatus(RegistrationSyncStatus.SYNC_FAILED);
        entity.setSyncErrorCode("UTM_REGISTRY_SYNC_FAILED");
        entity.setSyncErrorMessage(ex.getMessage());
        entity.setLastSyncedAt(now);
        entity.setResponsePayloadSnapshot(null);
    }

    private PilotRegistrationStatusDto saveAndPublish(PilotRegistrationEntity entity) {
        PilotRegistrationEntity saved = pilotRegistrationRepository.save(entity);
        PilotRegistrationStatusDto dto = pilotRegistrationMapper.toDto(saved);
        publisher.publish(dto);
        return dto;
    }

    private String toJson(Object value) {
        try { return value == null ? null : objectMapper.writeValueAsString(value); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("FAILED_TO_SERIALIZE_REGISTRATION_SNAPSHOT", ex); }
    }
}
