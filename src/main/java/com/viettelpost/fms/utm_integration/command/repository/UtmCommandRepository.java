package com.viettelpost.fms.utm_integration.command.repository;

import com.viettelpost.fms.utm_integration.command.domain.UtmCommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtmCommandRepository extends JpaRepository<UtmCommandEntity, String> {

    Optional<UtmCommandEntity> findByCommandId(String commandId);
}
