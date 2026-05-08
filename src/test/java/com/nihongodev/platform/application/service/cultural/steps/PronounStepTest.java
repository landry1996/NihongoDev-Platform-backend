package com.nihongodev.platform.application.service.cultural.steps;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PronounStep")
class PronounStepTest {

    private final PronounStep step = new PronounStep();

    @Test
    @DisplayName("should detect 俺 as CRITICAL in formal context")
    void shouldDetectOreAsCritical() {
        List<KeigoViolation> violations = step.analyze(
                "俺がやります", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(violations).isNotEmpty();
        assertThat(violations.get(0).severity()).isEqualTo(Severity.CRITICAL);
        assertThat(violations.get(0).rule()).contains("ore");
    }

    @Test
    @DisplayName("should detect 僕 as MINOR in formal context")
    void shouldDetectBokuAsMinor() {
        List<KeigoViolation> violations = step.analyze(
                "僕が担当します", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(violations).isNotEmpty();
        assertThat(violations.get(0).severity()).isEqualTo(Severity.MINOR);
    }

    @Test
    @DisplayName("should detect あなた as MODERATE")
    void shouldDetectAnataAsModerate() {
        List<KeigoViolation> violations = step.analyze(
                "あなたの意見は", KeigoLevel.TEINEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(violations).isNotEmpty();
        assertThat(violations.get(0).severity()).isEqualTo(Severity.MODERATE);
    }

    @Test
    @DisplayName("should not flag 私")
    void shouldNotFlagWatashi() {
        List<KeigoViolation> violations = step.analyze(
                "私が確認いたします", KeigoLevel.KENJOUGO, RelationshipType.TO_CLIENT);
        assertThat(violations).isEmpty();
    }
}
