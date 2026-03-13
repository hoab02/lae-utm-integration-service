package com.viettelpost.fms.utm_integration.registry.repository;

import com.viettelpost.fms.utm_integration.registry.domain.DroneRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DroneRegistrationRepository extends JpaRepository<DroneRegistrationEntity, String> {

    Optional<DroneRegistrationEntity> findByDroneId(String droneId);
}
