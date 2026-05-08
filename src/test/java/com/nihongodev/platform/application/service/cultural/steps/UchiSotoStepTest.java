package com.nihongodev.platform.application.service.cultural.steps;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UchiSotoStep")
class UchiSotoStepTest {

    private final UchiSotoStep step = new UchiSotoStep();

    @Test
    @DisplayName("should detect うちの会社 toward client as CRITICAL")
    void shouldDetectUchiNoKaisha() {
        List<KeigoViolation> violations = step.analyze(
                "うちの会社では対応します", KeigoLevel.KENJOUGO, RelationshipType.TO_CLIENT);
        assertThat(violations).isNotEmpty();
        assertThat(violations.get(0).severity()).isEqualTo(Severity.CRITICAL);
        assertThat(violations.get(0).rule()).contains("uchi_soto_company_self");
    }

    @Test
    @DisplayName("should detect あなたの会社 toward client as CRITICAL")
    void shouldDetectAnataNoKaisha() {
        List<KeigoViolation> violations = step.analyze(
                "あなたの会社のご方針", KeigoLevel.KENJOUGO, RelationshipType.TO_CLIENT);
        assertThat(violations).isNotEmpty();
        assertThat(violations.get(0).severity()).isEqualTo(Severity.CRITICAL);
        assertThat(violations.get(0).rule()).contains("uchi_soto_company_other");
    }

    @Test
    @DisplayName("should detect honorific on own superior toward external")
    void shouldDetectHonorificOnOwnSuperior() {
        List<KeigoViolation> violations = step.analyze(
                "うちの社長がおっしゃっていました", KeigoLevel.KENJOUGO, RelationshipType.TO_CLIENT);
        assertThat(violations).anyMatch(v -> v.rule().contains("uchi_soto_superior_to_external"));
    }

    @Test
    @DisplayName("should not flag correct usage of 弊社 toward client")
    void shouldNotFlagCorrectHeisha() {
        List<KeigoViolation> violations = step.analyze(
                "弊社では来月リリース予定です", KeigoLevel.KENJOUGO, RelationshipType.TO_CLIENT);
        assertThat(violations).isEmpty();
    }
}
