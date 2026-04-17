package com.viettelpost.fms.utm_integration.registry.service;

import com.viettelpost.fms.utm_integration.registry.client.DroneRegistryBasicInfo;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistryRecord;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistrySpec;
import com.viettelpost.fms.utm_integration.registry.client.DroneRegistryUpsertRequest;
import com.viettelpost.fms.utm_integration.registry.domain.DroneRegistrationEntity;
import com.viettelpost.fms.utm_integration.registry.domain.RegistrationSyncStatus;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationSubmitRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DroneRegistrationMapper {

    public DroneRegistryUpsertRequest toUpsertRequest(DroneRegistrationSubmitRequest request) {
        DroneRegistryBasicInfo basicInfo = new DroneRegistryBasicInfo(
                request.getBasicInfo().getManufacturer(),
                request.getBasicInfo().getModel(),
                request.getBasicInfo().getSerialNumber(),
                request.getBasicInfo().getRegistrationId(),
                request.getBasicInfo().getType(),
                request.getBasicInfo().getStandard(),
                request.getBasicInfo().getFactoryNumber(),
                request.getBasicInfo().getRegistrationIssuedAt(),
                request.getBasicInfo().getEngineType(),
                request.getBasicInfo().getRadioWorkInfo(),
                request.getBasicInfo().getControlMethod(),
                request.getBasicInfo().getProposeUsage()
        );

        DroneRegistrySpec spec = new DroneRegistrySpec(
                request.getSpec().getMassKg(),
                request.getSpec().getMaxPayloadKg(),
                request.getSpec().getMaxBatteryCapacity(),
                request.getSpec().getMaxPowerW(),
                request.getSpec().getMaxSpeedMps(),
                request.getSpec().getSizeM(),
                request.getSpec().getMaxTakeoffWeightKg(),
                request.getSpec().getTimeForOnceFly()
        );

        return new DroneRegistryUpsertRequest(
                request.getDroneStatus() == null ? null : String.valueOf(request.getDroneStatus()),
                request.getImage(),
                basicInfo,
                spec
        );
    }

    public void applySubmitRequest(DroneRegistrationEntity entity, DroneRegistrationSubmitRequest request) {
        entity.setDroneStatus(request.getDroneStatus());
        entity.setSerialNumber(request.getBasicInfo() == null ? null : request.getBasicInfo().getSerialNumber());
        entity.setRegistrationId(request.getBasicInfo() == null ? null : request.getBasicInfo().getRegistrationId());
    }

    public void applyUtmRecord(DroneRegistrationEntity entity, DroneRegistryRecord record) {
        entity.setUtmDroneId(record.id());

        // Không ép map statusLegit vào RegistrationStatus cũ
        entity.setStatus(null);

        entity.setDroneStatus(record.droneStatus());
        entity.setStatusLegit(record.statusLegit());
        entity.setStatusLegitNote(record.statusLegitNote());
        entity.setRegistrationId(record.basicInfo() == null ? null : record.basicInfo().registrationId());
        entity.setSerialNumber(record.basicInfo() == null ? null : record.basicInfo().serialNumber());
    }

    public DroneRegistrationStatusDto toDto(DroneRegistrationEntity entity) {
        return DroneRegistrationStatusDto.builder()
                .id(entity.getId())
                .utmDroneId(entity.getUtmDroneId())
                .syncStatus(entity.getSyncStatus())
                .status(entity.getStatus())
                .submittedAt(entity.getSubmittedAt())
                .approvedAt(entity.getApprovedAt())
                .rejectedAt(entity.getRejectedAt())
                .rejectReason(entity.getRejectReason())
                .droneStatus(entity.getDroneStatus())
                .statusLegit(entity.getStatusLegit())
                .statusLegitNote(entity.getStatusLegitNote())
                .registrationId(entity.getRegistrationId())
                .serialNumber(entity.getSerialNumber())
                .lastSyncedAt(entity.getLastSyncedAt())
                .errorCode(entity.getSyncErrorCode())
                .errorMessage(entity.getSyncErrorMessage())
                .build();
    }

    public DroneRegistrationStatusDto toUtmDto(DroneRegistryRecord record) {
        return DroneRegistrationStatusDto.builder()
                .utmDroneId(record.id())
                .syncStatus(RegistrationSyncStatus.SYNCED)

                // Không set String vào field enum
                .status(null)

                .droneStatus(record.droneStatus())
                .statusLegit(record.statusLegit())
                .statusLegitNote(record.statusLegitNote())
                .registrationId(record.basicInfo() == null ? null : record.basicInfo().registrationId())
                .serialNumber(record.basicInfo() == null ? null : record.basicInfo().serialNumber())
                .lastSyncedAt(new Date())
                .build();
    }
}