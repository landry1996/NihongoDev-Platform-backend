package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SubmitScenarioResponseCommand;
import com.nihongodev.platform.application.dto.ScenarioAttemptDto;
import com.nihongodev.platform.application.port.in.SubmitScenarioResponsePort;
import com.nihongodev.platform.application.port.out.CulturalProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.ScenarioAttemptRepositoryPort;
import com.nihongodev.platform.application.port.out.ScenarioRepositoryPort;
import com.nihongodev.platform.application.service.cultural.ScenarioEvaluator;
import com.nihongodev.platform.application.service.cultural.ScenarioEvaluatorFactory;
import com.nihongodev.platform.domain.event.ScenarioCompletedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CulturalProgress;
import com.nihongodev.platform.domain.model.CulturalScenario;
import com.nihongodev.platform.domain.model.ScenarioAttempt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubmitScenarioResponseUseCase implements SubmitScenarioResponsePort {

    private final ScenarioRepositoryPort scenarioRepository;
    private final ScenarioAttemptRepositoryPort attemptRepository;
    private final CulturalProgressRepositoryPort progressRepository;
    private final ScenarioEvaluatorFactory evaluatorFactory;
    private final EventPublisherPort eventPublisher;

    public SubmitScenarioResponseUseCase(ScenarioRepositoryPort scenarioRepository,
                                         ScenarioAttemptRepositoryPort attemptRepository,
                                         CulturalProgressRepositoryPort progressRepository,
                                         ScenarioEvaluatorFactory evaluatorFactory,
                                         EventPublisherPort eventPublisher) {
        this.scenarioRepository = scenarioRepository;
        this.attemptRepository = attemptRepository;
        this.progressRepository = progressRepository;
        this.evaluatorFactory = evaluatorFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ScenarioAttemptDto submit(UUID userId, SubmitScenarioResponseCommand command) {
        CulturalScenario scenario = scenarioRepository.findById(command.scenarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Scenario", "id", command.scenarioId()));

        ScenarioEvaluator evaluator = evaluatorFactory.getEvaluator(scenario.getMode());
        ScenarioEvaluator.EvaluationResult result = evaluator.evaluate(scenario, command.response(), command.selectedChoiceId());

        ScenarioAttempt attempt = ScenarioAttempt.create(
                userId, scenario.getId(), command.response(), command.selectedChoiceId(), command.timeSpentSeconds());
        attempt.applyScore(result.score(), result.violations(), result.feedback());

        attemptRepository.save(attempt);

        updateProgress(userId, scenario, result.score().overallScore());

        eventPublisher.publish("cultural-events", ScenarioCompletedEvent.create(
                userId, scenario.getId(), result.score().overallScore(), scenario.getCategory()));

        return new ScenarioAttemptDto(
                attempt.getId(), scenario.getId(), scenario.getTitle(),
                attempt.getUserResponse(), attempt.getScore(), attempt.getViolations(),
                attempt.getFeedback(), scenario.getModelAnswer(), scenario.getModelAnswerExplanation(),
                attempt.getTimeSpentSeconds(), attempt.getAttemptedAt()
        );
    }

    private void updateProgress(UUID userId, CulturalScenario scenario, int overallScore) {
        CulturalProgress progress = progressRepository.findByUserIdAndCategory(userId, scenario.getCategory())
                .orElseGet(() -> CulturalProgress.initialize(userId, scenario.getCategory()));
        progress.recordAttempt(overallScore);
        progressRepository.save(progress);
    }
}
