package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CulturalScenarioDto;
import com.nihongodev.platform.domain.model.*;

import java.util.List;
import java.util.UUID;

public interface GetScenarioCatalogPort {
    List<CulturalScenarioDto> getPublished(WorkplaceContext context, JapaneseLevel difficulty, ScenarioCategory category);
    CulturalScenarioDto getById(UUID scenarioId);
}
