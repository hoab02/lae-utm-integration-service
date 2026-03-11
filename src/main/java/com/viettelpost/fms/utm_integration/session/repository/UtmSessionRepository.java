package com.viettelpost.fms.utm_integration.session.repository;

import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtmSessionRepository extends JpaRepository<UtmSessionEntity, String> {

    Optional<UtmSessionEntity> findTopByOrderByCreatedDateDesc();
}
