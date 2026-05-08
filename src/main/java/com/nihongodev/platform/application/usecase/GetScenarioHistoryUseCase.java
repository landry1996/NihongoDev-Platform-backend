package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ScenarioAttemptDto;
import com.nihongodev.platform.application.port.in.GetScenarioHistoryPort;
import com.nihongodev.platform.application.port.out.ScenarioAttemptRepositoryPort;
import com.nihongodev.platform.application.port.out.ScenarioRepositoryPort;
import com.nihongodev.platform.domain.model.CulturalScenario;
import com.nihongodev.platform.domain.model.ScenarioAttempt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetScenarioHistoryUseCase implements GetScenarioHistoryPort {

    private final ScenarioAttemptRepositoryPort attemptRepository;
    private final ScenarioRepositoryPort scenarioRepository;

    public GetScenarioHistoryUseCase(ScenarioAttemptRepositoryPort attemptRepository,
                                     ScenarioRepositoryPort scenarioRepository) {
        this.attemptRepository = attemptRepository;
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public List<ScenarioAttemptDto> getUserHistory(UUID userId) {
        return attemptRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    private ScenarioAttemptDto toDto(ScenarioAttempt attempt) {
        CulturalScenario scenario = scenarioRepository.findById(attempt.getScenarioId())
                .orElse(null);

        String title = scenario != null ? scenario.getTitle() : "Unknown scenario";
        String modelAnswer = scenario != null ? scenario.getModelAnswer() : null;
        String explanation = scenario != null ? scenario.getModelAnswerExplanation() : null;

        return new ScenarioAttemptDto(
                attempt.getId(), attempt.getScenarioId(), title,
                attempt.getUserResponse(), attempt.getScore(), attempt.getViolations(),
                attempt.getFeedback(), modelAnswer, explanation,
                attempt.getTimeSpentSeconds(), attempt.getAttemptedAt()
        );
    }
}
