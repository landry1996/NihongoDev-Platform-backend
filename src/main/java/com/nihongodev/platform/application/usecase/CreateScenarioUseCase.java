package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateScenarioCommand;
import com.nihongodev.platform.application.dto.CulturalScenarioDto;
import com.nihongodev.platform.application.port.in.CreateScenarioPort;
import com.nihongodev.platform.application.port.out.ScenarioRepositoryPort;
import com.nihongodev.platform.domain.model.CulturalScenario;
import org.springframework.stereotype.Service;

@Service
public class CreateScenarioUseCase implements CreateScenarioPort {

    private final ScenarioRepositoryPort scenarioRepository;

    public CreateScenarioUseCase(ScenarioRepositoryPort scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public CulturalScenarioDto create(CreateScenarioCommand command) {
        CulturalScenario scenario = CulturalScenario.create(
                command.title(), command.titleJp(), command.situation(), command.situationJp(),
                command.context(), command.relationship(), command.mode(), command.category(),
                command.expectedKeigoLevel(), command.difficulty(), command.xpReward()
        );
        scenario.setChoices(command.choices());
        scenario.setModelAnswer(command.modelAnswer());
        scenario.setModelAnswerExplanation(command.modelAnswerExplanation());
        scenario.setKeyPhrases(command.keyPhrases());
        scenario.setAvoidPhrases(command.avoidPhrases());
        scenario.setCulturalNote(command.culturalNote());
        scenario.setPublished(true);

        CulturalScenario saved = scenarioRepository.save(scenario);

        return new CulturalScenarioDto(
                saved.getId(), saved.getTitle(), saved.getTitleJp(),
                saved.getSituation(), saved.getSituationJp(),
                saved.getContext(), saved.getRelationship(), saved.getMode(),
                saved.getCategory(), saved.getExpectedKeigoLevel(), saved.getDifficulty(),
                saved.getChoices(), saved.getCulturalNote(), saved.getXpReward()
        );
    }
}
