package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioRepositoryPort {
    CulturalScenario save(CulturalScenario scenario);
    Optional<CulturalScenario> findById(UUID id);
    List<CulturalScenario> findPublished(WorkplaceContext context, JapaneseLevel difficulty, ScenarioCategory category);
}
