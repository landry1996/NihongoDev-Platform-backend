package com.nihongodev.platform.application.service.cultural;

import com.nihongodev.platform.application.service.cultural.steps.*;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("KeigoValidator")
class KeigoValidatorTest {

    private KeigoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new KeigoValidator(List.of(
                new VerbFormStep(),
                new PronounStep(),
                new HonorificPrefixStep(),
                new SentenceEndingStep(),
                new UchiSotoStep(),
                new BusinessExpressionStep()
        ));
    }

    @Test
    @DisplayName("should return perfect score for null text")
    void shouldReturnPerfectForNull() {
        KeigoValidationResult result = validator.validate(null, KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(result.score()).isEqualTo(100);
        assertThat(result.violations()).isEmpty();
    }

    @Test
    @DisplayName("should return perfect score for blank text")
    void shouldReturnPerfectForBlank() {
        KeigoValidationResult result = validator.validate("  ", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(result.score()).isEqualTo(100);
    }

    @Test
    @DisplayName("should return 100 for text with no violations")
    void shouldReturnHundredForCleanText() {
        KeigoValidationResult result = validator.validate(
                "いらっしゃいますか", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(result.score()).isEqualTo(100);
    }

    @Test
    @DisplayName("should detect pronoun violation with 俺")
    void shouldDetectCasualPronoun() {
        KeigoValidationResult result = validator.validate(
                "俺がやります", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(result.violations()).isNotEmpty();
        assertThat(result.score()).isLessThan(100);
    }

    @Test
    @DisplayName("should accumulate multiple violations")
    void shouldAccumulateViolations() {
        KeigoValidationResult result = validator.validate(
                "俺がやるよ", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(result.violations().size()).isGreaterThanOrEqualTo(2);
        assertThat(result.score()).isLessThan(80);
    }

    @Test
    @DisplayName("should never go below 0")
    void shouldNeverGoBelowZero() {
        KeigoValidationResult result = validator.validate(
                "俺がやるよ。あんたの会社のうちの部長がさ", KeigoLevel.SONKEIGO, RelationshipType.TO_CLIENT);
        assertThat(result.score()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("should detect uchi/soto violation with うちの会社 toward client")
    void shouldDetectUchiSotoViolation() {
        KeigoValidationResult result = validator.validate(
                "うちの会社では対応できます", KeigoLevel.KENJOUGO, RelationshipType.TO_CLIENT);
        assertThat(result.violations()).anyMatch(v -> v.rule().contains("uchi_soto"));
    }

    @Test
    @DisplayName("should penalize CRITICAL more than MODERATE")
    void shouldPenalizeBySeverity() {
        // 俺 is CRITICAL, あなた is MODERATE
        KeigoValidationResult resultCritical = validator.validate(
                "俺は行きます", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        KeigoValidationResult resultModerate = validator.validate(
                "あなたは行きます", KeigoLevel.SONKEIGO, RelationshipType.TO_SUPERIOR);
        assertThat(resultCritical.score()).isLessThanOrEqualTo(resultModerate.score());
    }
}
