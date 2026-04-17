package com.viettelpost.fms.utm_integration.registry.repository;

import com.viettelpost.fms.utm_integration.registry.domain.PilotRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PilotRegistrationRepository extends JpaRepository<PilotRegistrationEntity, String> {

    Optional<PilotRegistrationEntity> findByUtmPilotId(String utmPilotId);

    Optional<PilotRegistrationEntity> findByPersonalIdNumber(String personalIdNumber);

    Optional<PilotRegistrationEntity> findByLicenseNumber(String licenseNumber);
}
