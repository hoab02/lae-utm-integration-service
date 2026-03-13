package com.viettelpost.fms.utm_integration.mission.repository;

import com.viettelpost.fms.utm_integration.mission.domain.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<MissionEntity, String> {

    Optional<MissionEntity> findByMissionId(String missionId);
}