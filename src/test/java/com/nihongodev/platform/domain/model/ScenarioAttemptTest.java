package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScenarioAttempt Domain Model")
class ScenarioAttemptTest {

    @Test
    @DisplayName("should create attempt with correct fields")
    void shouldCreateWithCorrectFields() {
        UUID userId = UUID.randomUUID();
        UUID scenarioId = UUID.randomUUID();
        UUID choiceId = UUID.randomUUID();

        ScenarioAttempt attempt = ScenarioAttempt.create(userId, scenarioId, "お疲れ様です", choiceId, 45);

        assertThat(attempt.getId()).isNotNull();
        assertThat(attempt.getUserId()).isEqualTo(userId);
        assertThat(attempt.getScenarioId()).isEqualTo(scenarioId);
        assertThat(attempt.getUserResponse()).isEqualTo("お疲れ様です");
        assertThat(attempt.getSelectedChoiceId()).isEqualTo(choiceId);
        assertThat(attempt.getTimeSpentSeconds()).isEqualTo(45);
        assertThat(attempt.getAttemptedAt()).isNotNull();
    }

    @Test
    @DisplayName("should apply score, violations, and feedback")
    void shouldApplyScore() {
        ScenarioAttempt attempt = ScenarioAttempt.create(
                UUID.randomUUID(), UUID.randomUUID(), "ご確認お願いします", null, 30);

        CulturalScore score = CulturalScore.calculate(85, 90, 70, 75, 80);
        List<KeigoViolation> violations = List.of(
                new KeigoViolation("お願いします", "お願いいたします",
                        KeigoLevel.TEINEIGO, KeigoLevel.KENJOUGO,
                        "kenjougo_required", Severity.MINOR)
        );

        attempt.applyScore(score, violations, "Good but use more humble forms");

        assertThat(attempt.getScore()).isNotNull();
        assertThat(attempt.getScore().keigoScore()).isEqualTo(85);
        assertThat(attempt.getViolations()).hasSize(1);
        assertThat(attempt.getViolations().get(0).originalText()).isEqualTo("お願いします");
        assertThat(attempt.getFeedback()).isEqualTo("Good but use more humble forms");
    }

    @Test
    @DisplayName("should return correct values from getters")
    void shouldReturnCorrectGetterValues() {
        UUID userId = UUID.randomUUID();
        UUID scenarioId = UUID.randomUUID();

        ScenarioAttempt attempt = ScenarioAttempt.create(userId, scenarioId, "失礼いたします", null, 120);

        assertThat(attempt.getUserId()).isEqualTo(userId);
        assertThat(attempt.getScenarioId()).isEqualTo(scenarioId);
        assertThat(attempt.getUserResponse()).isEqualTo("失礼いたします");
        assertThat(attempt.getTimeSpentSeconds()).isEqualTo(120);
    }
}
