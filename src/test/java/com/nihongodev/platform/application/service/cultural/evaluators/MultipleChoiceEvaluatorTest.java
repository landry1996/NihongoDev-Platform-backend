package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.application.service.cultural.ScenarioEvaluator;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MultipleChoiceEvaluator")
class MultipleChoiceEvaluatorTest {

    private final MultipleChoiceEvaluator evaluator = new MultipleChoiceEvaluator();

    private CulturalScenario createScenario(List<ScenarioChoice> choices) {
        CulturalScenario scenario = CulturalScenario.create(
                "Test", "テスト", "Situation", "状況",
                WorkplaceContext.MEETING, RelationshipType.TO_SUPERIOR,
                ScenarioMode.MULTIPLE_CHOICE, ScenarioCategory.COMMUNICATION,
                KeigoLevel.SONKEIGO, JapaneseLevel.N3, 50);
        scenario.setChoices(choices);
        return scenario;
    }

    @Test
    @DisplayName("should return score based on selected choice")
    void shouldReturnScoreFromChoice() {
        CulturalScenario scenario = createScenario(List.of(
                new ScenarioChoice("Good", "良い", true, 90, "Excellent!"),
                new ScenarioChoice("Bad", "悪い", false, 30, "Too casual")
        ));

        // With null selectedChoiceId, defaults to index 0 (the "Good" choice)
        ScenarioEvaluator.EvaluationResult result = evaluator.evaluate(scenario, null, null);
        assertThat(result.score().overallScore()).isEqualTo(90);
    }

    @Test
    @DisplayName("should include feedback with optimal answer hint when non-optimal selected")
    void shouldIncludeOptimalInFeedback() {
        CulturalScenario scenario = createScenario(List.of(
                new ScenarioChoice("Bad answer", "悪い答え", false, 30, "Wrong approach."),
                new ScenarioChoice("Good answer", "良い答え", true, 95, "Perfect!")
        ));

        // null selectedChoiceId defaults to index 0 which is the non-optimal choice
        ScenarioEvaluator.EvaluationResult result = evaluator.evaluate(scenario, null, null);
        assertThat(result.feedback()).contains("Wrong approach.");
        assertThat(result.feedback()).contains("良い答え");
    }

    @Test
    @DisplayName("should handle empty choices list")
    void shouldHandleEmptyChoices() {
        CulturalScenario scenario = createScenario(List.of());

        ScenarioEvaluator.EvaluationResult result = evaluator.evaluate(scenario, null, UUID.randomUUID());
        assertThat(result.score().overallScore()).isEqualTo(0);
        assertThat(result.feedback()).contains("No choices");
    }
}
