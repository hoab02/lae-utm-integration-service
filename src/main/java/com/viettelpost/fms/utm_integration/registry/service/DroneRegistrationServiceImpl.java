package com.viettelpost.fms.utm_integration.registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistryRecord;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistrySearchRequest;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistryUpsertRequest;
import com.viettelpost.fms.utm_integration.registry.client.UtmDroneRegistryClient;
import com.viettelpost.fms.utm_integration.registry.domain.DroneRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationSyncStatus;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationSubmitRequest;
import com.viettelpost.fms.utm_integration.registry.kafka.DroneRegistrationResultKafkaPublisher;
import com.viettelpost.fms.utm_integration.registry.repository.DroneRegistrationRepository;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionContextProvider;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroneRegistrationServiceImpl implements DroneRegistrationService {

    private final DroneRegistrationRepository droneRegistrationRepository;
    private final UtmDroneRegistryClient utmDroneRegistryClient;
    private final UtmSessionContextProvider utmSessionContextProvider;
    private final DroneRegistrationMapper droneRegistrationMapper;
    private final DroneRegistrationResultKafkaPublisher publisher;
    private final ObjectMapper objectMapper;

    @Override
    public DroneRegistrationStatusDto submit(DroneRegistrationSubmitRequest request) throws InternalException {
        DroneRegistrationEntity entity = droneRegistrationRepository.findBySerialNumber(request.getBasicInfo().getSerialNumber())
                .orElseGet(() -> newEntity(request.getBasicInfo().getSerialNumber()));

        return createAndPersist(entity, request);
    }

    @Override
    public DroneRegistrationStatusDto update(String serialNumber, DroneRegistrationSubmitRequest request) throws InternalException {
        DroneRegistrationEntity entity = droneRegistrationRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new InternalException(ErrorCode.ERROR_DRONE_REGISTRATION_NOT_FOUND));

        if (entity.getUtmDroneId() == null || entity.getUtmDroneId().isBlank()) {
            throw new InternalException(ErrorCode.ERROR_DRONE_REGISTRATION_NOT_FOUND);
        }

        return updateAndPersist(entity, request, entity.getUtmDroneId());
    }

    @Override
    public DroneRegistrationStatusDto getBySerialNumber(String serialNumber) throws InternalException {
        return droneRegistrationRepository.findBySerialNumber(serialNumber)
                .map(droneRegistrationMapper::toDto)
                .orElseThrow(() -> new InternalException(ErrorCode.ERROR_DRONE_REGISTRATION_NOT_FOUND));
    }

    @Override
    public DroneRegistrationStatusDto getByUtmDroneId(String utmDroneId) throws InternalException {
        UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
        DroneRegistryRecord record = utmDroneRegistryClient.getById(session.accessToken(), utmDroneId);
        if (record == null) {
            throw new InternalException(ErrorCode.ERROR_DRONE_REGISTRATION_NOT_FOUND);
        }
        return droneRegistrationMapper.toUtmDto(record);
    }

    @Override
    public List<DroneRegistrationStatusDto> searchUtm(String serialNumber, String registrationId) throws InternalException {
        UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
        return utmDroneRegistryClient.search(session.accessToken(), new DroneRegistrySearchRequest(serialNumber, registrationId))
                .stream()
                .map(droneRegistrationMapper::toUtmDto)
                .toList();
    }

    private DroneRegistrationStatusDto createAndPersist(DroneRegistrationEntity entity,
                                                        DroneRegistrationSubmitRequest request) throws InternalException {
        Date now = new Date();
        DroneRegistryUpsertRequest payload = droneRegistrationMapper.toUpsertRequest(request);
        markSyncing(entity, request, payload, now);

        try {
            UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
            DroneRegistryRecord record = utmDroneRegistryClient.create(session.accessToken(), payload);
            applySuccess(entity, record, now);
            return saveAndPublish(entity);
        } catch (InternalException ex) {
            applyFailure(entity, ex, now);
            saveAndPublish(entity);
            throw ex;
        } catch (RuntimeException ex) {
            applyFailure(entity, ex, now);
            saveAndPublish(entity);
            throw ex;
        }
    }

    private DroneRegistrationStatusDto updateAndPersist(DroneRegistrationEntity entity,
                                                        DroneRegistrationSubmitRequest request,
                                                        String utmDroneId) throws InternalException {
        Date now = new Date();
        DroneRegistryUpsertRequest payload = droneRegistrationMapper.toUpsertRequest(request);
        markSyncing(entity, request, payload, now);

        try {
            UtmSessionContextDto session = utmSessionContextProvider.getRequiredSessionContext();
            DroneRegistryRecord record = utmDroneRegistryClient.update(session.accessToken(), utmDroneId, payload);
            applySuccess(entity, record, now);
            return saveAndPublish(entity);
        } catch (InternalException ex) {
            applyFailure(entity, ex, now);
            saveAndPublish(entity);
            throw ex;
        } catch (RuntimeException ex) {
            applyFailure(entity, ex, now);
            saveAndPublish(entity);
            throw ex;
        }
    }

    private DroneRegistrationEntity newEntity(String serialNumber) {
        DroneRegistrationEntity entity = new DroneRegistrationEntity();
        entity.setSerialNumber(serialNumber);
        entity.setStatus(RegistrationStatus.DRAFT);
        entity.setSyncStatus(RegistrationSyncStatus.PENDING);
        return entity;
    }

    private void markSyncing(DroneRegistrationEntity entity,
                             DroneRegistrationSubmitRequest request,
                             Object payload,
                             Date now) {
        droneRegistrationMapper.applySubmitRequest(entity, request);
        entity.setSyncStatus(RegistrationSyncStatus.SYNCING);
        entity.setSyncErrorCode(null);
        entity.setSyncErrorMessage(null);
        entity.setRequestPayloadSnapshot(toJson(payload));
        entity.setSubmittedAt(now);
    }

    private void applySuccess(DroneRegistrationEntity entity, DroneRegistryRecord record, Date now) {
        droneRegistrationMapper.applyUtmRecord(entity, record);
        entity.setSyncStatus(RegistrationSyncStatus.SYNCED);
        entity.setSyncErrorCode(null);
        entity.setSyncErrorMessage(null);
        entity.setResponsePayloadSnapshot(toJson(record));
        entity.setLastSyncedAt(now);
        entity.setRejectReason(null);

        entity.setRejectedAt(null);
        entity.setApprovedAt(null);
    }

    private void applyFailure(DroneRegistrationEntity entity, Exception ex, Date now) {
        log.error("drone_registration_sync_failed serialNumber={} message={}", entity.getSerialNumber(), ex.getMessage(), ex);
        entity.setSyncStatus(RegistrationSyncStatus.SYNC_FAILED);
        entity.setSyncErrorCode("UTM_REGISTRY_SYNC_FAILED");
        entity.setSyncErrorMessage(ex.getMessage());
        entity.setLastSyncedAt(now);
        entity.setResponsePayloadSnapshot(null);
    }

    private DroneRegistrationStatusDto saveAndPublish(DroneRegistrationEntity entity) {
        DroneRegistrationEntity saved = droneRegistrationRepository.save(entity);
        DroneRegistrationStatusDto dto = droneRegistrationMapper.toDto(saved);
        publisher.publish(dto);
        return dto;
    }

    private String toJson(Object value) {
        try {
            return value == null ? null : objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("FAILED_TO_SERIALIZE_REGISTRATION_SNAPSHOT", ex);
        }
    }
}