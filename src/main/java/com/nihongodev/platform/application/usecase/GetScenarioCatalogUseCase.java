package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CulturalScenarioDto;
import com.nihongodev.platform.application.port.in.GetScenarioCatalogPort;
import com.nihongodev.platform.application.port.out.ScenarioRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetScenarioCatalogUseCase implements GetScenarioCatalogPort {

    private final ScenarioRepositoryPort scenarioRepository;

    public GetScenarioCatalogUseCase(ScenarioRepositoryPort scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public List<CulturalScenarioDto> getPublished(WorkplaceContext context, JapaneseLevel difficulty, ScenarioCategory category) {
        return scenarioRepository.findPublished(context, difficulty, category).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public CulturalScenarioDto getById(UUID scenarioId) {
        CulturalScenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario", "id", scenarioId));
        return toDto(scenario);
    }

    private CulturalScenarioDto toDto(CulturalScenario s) {
        return new CulturalScenarioDto(
                s.getId(), s.getTitle(), s.getTitleJp(), s.getSituation(), s.getSituationJp(),
                s.getContext(), s.getRelationship(), s.getMode(), s.getCategory(),
                s.getExpectedKeigoLevel(), s.getDifficulty(), s.getChoices(),
                s.getCulturalNote(), s.getXpReward()
        );
    }
}
