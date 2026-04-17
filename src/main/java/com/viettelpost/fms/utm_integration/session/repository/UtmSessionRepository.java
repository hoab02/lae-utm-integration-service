package com.viettelpost.fms.utm_integration.session.repository;

import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.domain.UtmSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtmSessionRepository extends JpaRepository<UtmSessionEntity, String> {

    Optional<UtmSessionEntity> findTopByOrderByCreatedDateDesc();

    Optional<UtmSessionEntity> findTopByStatusOrderByCreatedDateDesc(SessionStatus status);
}
