package com.nihongodev.platform.application.service.cultural.steps;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VerbFormStep")
class VerbFormStepTest {

    private final VerbFormStep step = new VerbFormStep();

    @Test
    @DisplayName("should not flag correct sonkeigo verb usage")
    void shouldNotFlagCorrectSonkeigo() {
        List<KeigoViolation> violations = step.analyze(
                "先生がいらっしゃいます", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("should detect casual 行く when SONKEIGO expected")
    void shouldDetectCasualVerbInSonkeigoContext() {
        List<KeigoViolation> violations = step.analyze(
                "先生が行く", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(violations).isNotEmpty();
        assertThat(violations.get(0).rule()).contains("sonkeigo");
    }

    @Test
    @DisplayName("should not flag casual verbs for TEINEIGO level")
    void shouldNotFlagCasualForTeineigo() {
        List<KeigoViolation> violations = step.analyze(
                "行くことにしました", KeigoLevel.TEINEIGO, RelationshipType.TO_PEER);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("should detect kenjougo violation when casual used instead of humble")
    void shouldDetectKenjougoViolation() {
        List<KeigoViolation> violations = step.analyze(
                "私が見る", KeigoLevel.KENJOUGO, RelationshipType.TO_CLIENT);
        assertThat(violations).isNotEmpty();
        assertThat(violations.get(0).suggestion()).contains("拝見する");
    }

    @Test
    @DisplayName("should return empty list for empty text")
    void shouldReturnEmptyForEmptyText() {
        List<KeigoViolation> violations = step.analyze(
                "", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(violations).isEmpty();
    }
}
