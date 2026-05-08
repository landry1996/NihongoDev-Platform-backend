package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CulturalScenarioDto;
import com.nihongodev.platform.application.port.in.StartScenarioPort;
import com.nihongodev.platform.application.port.out.ScenarioRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CulturalScenario;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StartScenarioUseCase implements StartScenarioPort {

    private final ScenarioRepositoryPort scenarioRepository;

    public StartScenarioUseCase(ScenarioRepositoryPort scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public CulturalScenarioDto startScenario(UUID userId, UUID scenarioId) {
        CulturalScenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario", "id", scenarioId));

        return new CulturalScenarioDto(
                scenario.getId(), scenario.getTitle(), scenario.getTitleJp(),
                scenario.getSituation(), scenario.getSituationJp(),
                scenario.getContext(), scenario.getRelationship(),
                scenario.getMode(), scenario.getCategory(),
                scenario.getExpectedKeigoLevel(), scenario.getDifficulty(),
                scenario.getChoices(), scenario.getCulturalNote(), scenario.getXpReward()
        );
    }
}
