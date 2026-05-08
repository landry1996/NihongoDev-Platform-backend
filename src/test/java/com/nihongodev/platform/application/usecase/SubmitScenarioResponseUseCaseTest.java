package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SubmitScenarioResponseCommand;
import com.nihongodev.platform.application.dto.ScenarioAttemptDto;
import com.nihongodev.platform.application.port.out.CulturalProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.ScenarioAttemptRepositoryPort;
import com.nihongodev.platform.application.port.out.ScenarioRepositoryPort;
import com.nihongodev.platform.application.service.cultural.ScenarioEvaluator;
import com.nihongodev.platform.application.service.cultural.ScenarioEvaluatorFactory;
import com.nihongodev.platform.application.service.cultural.evaluators.FreeTextEvaluator;
import com.nihongodev.platform.application.service.cultural.evaluators.MultipleChoiceEvaluator;
import com.nihongodev.platform.application.service.cultural.evaluators.RolePlayEvaluator;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmitScenarioResponseUseCase")
class SubmitScenarioResponseUseCaseTest {

    @Mock private ScenarioRepositoryPort scenarioRepository;
    @Mock private ScenarioAttemptRepositoryPort attemptRepository;
    @Mock private CulturalProgressRepositoryPort progressRepository;
    @Mock private EventPublisherPort eventPublisher;
    @Mock private ScenarioEvaluator mockEvaluator;

    private SubmitScenarioResponseUseCase useCase;

    @BeforeEach
    void setUp() {
        // Create factory with the mock evaluator for all modes
        ScenarioEvaluatorFactory factory = new ScenarioEvaluatorFactory(
                (MultipleChoiceEvaluator) null, (FreeTextEvaluator) null, (RolePlayEvaluator) null
        ) {
            @Override
            public ScenarioEvaluator getEvaluator(ScenarioMode mode) {
                return mockEvaluator;
            }
        };
        useCase = new SubmitScenarioResponseUseCase(
                scenarioRepository, attemptRepository, progressRepository, factory, eventPublisher);
    }

    private CulturalScenario createScenario() {
        CulturalScenario scenario = CulturalScenario.create(
                "Test", "テスト", "Situation", "状況",
                WorkplaceContext.MEETING, RelationshipType.TO_SUPERIOR,
                ScenarioMode.FREE_TEXT, ScenarioCategory.COMMUNICATION,
                KeigoLevel.SONKEIGO, JapaneseLevel.N3, 50);
        scenario.setModelAnswer("モデル回答");
        scenario.setModelAnswerExplanation("解説");
        return scenario;
    }

    @Test
    @DisplayName("should evaluate and save attempt for valid scenario")
    void shouldEvaluateAndSave() {
        UUID userId = UUID.randomUUID();
        CulturalScenario scenario = createScenario();

        when(scenarioRepository.findById(scenario.getId())).thenReturn(Optional.of(scenario));
        when(mockEvaluator.evaluate(any(), any(), any())).thenReturn(
                new ScenarioEvaluator.EvaluationResult(CulturalScore.calculate(80, 75, 70, 65, 60), List.of(), "Good"));
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(progressRepository.findByUserIdAndCategory(eq(userId), any()))
                .thenReturn(Optional.of(CulturalProgress.initialize(userId, ScenarioCategory.COMMUNICATION)));
        when(progressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitScenarioResponseCommand command = new SubmitScenarioResponseCommand(scenario.getId(), "回答", null, 30);
        ScenarioAttemptDto result = useCase.submit(userId, command);

        assertThat(result).isNotNull();
        verify(attemptRepository).save(any());
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException for non-existent scenario")
    void shouldThrowForNonExistentScenario() {
        UUID scenarioId = UUID.randomUUID();
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

        SubmitScenarioResponseCommand command = new SubmitScenarioResponseCommand(scenarioId, "text", null, 10);

        assertThatThrownBy(() -> useCase.submit(UUID.randomUUID(), command))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("should update progress after submission")
    void shouldUpdateProgress() {
        UUID userId = UUID.randomUUID();
        CulturalScenario scenario = createScenario();

        when(scenarioRepository.findById(any())).thenReturn(Optional.of(scenario));
        when(mockEvaluator.evaluate(any(), any(), any())).thenReturn(
                new ScenarioEvaluator.EvaluationResult(CulturalScore.calculate(80, 80, 80, 80, 80), List.of(), "OK"));
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(progressRepository.findByUserIdAndCategory(eq(userId), any()))
                .thenReturn(Optional.of(CulturalProgress.initialize(userId, ScenarioCategory.COMMUNICATION)));
        when(progressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitScenarioResponseCommand command = new SubmitScenarioResponseCommand(scenario.getId(), "text", null, 20);
        useCase.submit(userId, command);

        verify(progressRepository).save(any(CulturalProgress.class));
    }

    @Test
    @DisplayName("should publish event after submission")
    void shouldPublishEvent() {
        UUID userId = UUID.randomUUID();
        CulturalScenario scenario = createScenario();

        when(scenarioRepository.findById(any())).thenReturn(Optional.of(scenario));
        when(mockEvaluator.evaluate(any(), any(), any())).thenReturn(
                new ScenarioEvaluator.EvaluationResult(CulturalScore.calculate(70, 70, 70, 70, 70), List.of(), "OK"));
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(progressRepository.findByUserIdAndCategory(eq(userId), any()))
                .thenReturn(Optional.of(CulturalProgress.initialize(userId, ScenarioCategory.COMMUNICATION)));
        when(progressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitScenarioResponseCommand command = new SubmitScenarioResponseCommand(scenario.getId(), "text", null, 20);
        useCase.submit(userId, command);

        verify(eventPublisher).publish(eq("cultural-events"), any());
    }

    @Test
    @DisplayName("should return DTO with correct score")
    void shouldReturnDtoWithScore() {
        UUID userId = UUID.randomUUID();
        CulturalScenario scenario = createScenario();

        when(scenarioRepository.findById(any())).thenReturn(Optional.of(scenario));
        when(mockEvaluator.evaluate(any(), any(), any())).thenReturn(
                new ScenarioEvaluator.EvaluationResult(CulturalScore.calculate(90, 85, 80, 75, 70), List.of(), "Excellent"));
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(progressRepository.findByUserIdAndCategory(eq(userId), any()))
                .thenReturn(Optional.of(CulturalProgress.initialize(userId, ScenarioCategory.COMMUNICATION)));
        when(progressRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitScenarioResponseCommand command = new SubmitScenarioResponseCommand(scenario.getId(), "text", null, 20);
        ScenarioAttemptDto result = useCase.submit(userId, command);

        assertThat(result.score().keigoScore()).isEqualTo(90);
        assertThat(result.feedback()).isEqualTo("Excellent");
    }
}
