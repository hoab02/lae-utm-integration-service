package com.viettelpost.fms.utm_integration.airspace.repository;

import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateEntity;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateStatus;
import com.viettelpost.fms.utm_integration.airspace.domain.AirspaceUpdateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirspaceUpdateRepository extends JpaRepository<AirspaceUpdateEntity, String> {

    Optional<AirspaceUpdateEntity> findByUpdateId(String updateId);

    Optional<AirspaceUpdateEntity> findTopByTypeAndStatusOrderByEffectiveFromDescReceivedAtDesc(
            AirspaceUpdateType type,
            AirspaceUpdateStatus status
    );
}
