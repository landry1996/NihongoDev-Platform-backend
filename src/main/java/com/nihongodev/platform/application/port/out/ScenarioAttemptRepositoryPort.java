package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.ScenarioAttempt;

import java.util.List;
import java.util.UUID;

public interface ScenarioAttemptRepositoryPort {
    ScenarioAttempt save(ScenarioAttempt attempt);
    List<ScenarioAttempt> findByUserId(UUID userId);
    List<ScenarioAttempt> findByUserIdAndScenarioId(UUID userId, UUID scenarioId);
}
