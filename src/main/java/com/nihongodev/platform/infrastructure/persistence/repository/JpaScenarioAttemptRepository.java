package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.ScenarioAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaScenarioAttemptRepository extends JpaRepository<ScenarioAttemptEntity, UUID> {
    List<ScenarioAttemptEntity> findByUserId(UUID userId);
    List<ScenarioAttemptEntity> findByUserIdAndScenarioId(UUID userId, UUID scenarioId);
}
