package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.CulturalProgress;
import com.nihongodev.platform.domain.model.ScenarioCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CulturalProgressRepositoryPort {
    CulturalProgress save(CulturalProgress progress);
    Optional<CulturalProgress> findByUserIdAndCategory(UUID userId, ScenarioCategory category);
    List<CulturalProgress> findByUserId(UUID userId);
}
