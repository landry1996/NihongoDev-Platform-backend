package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.application.service.cultural.KeigoValidator;
import com.nihongodev.platform.application.service.cultural.ScenarioEvaluator;
import com.nihongodev.platform.application.service.cultural.steps.*;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FreeTextEvaluator")
class FreeTextEvaluatorTest {

    private FreeTextEvaluator evaluator;

    @BeforeEach
    void setUp() {
        KeigoValidator keigoValidator = new KeigoValidator(List.of(
                new VerbFormStep(), new PronounStep(), new HonorificPrefixStep(),
                new SentenceEndingStep(), new UchiSotoStep(), new BusinessExpressionStep()
        ));
        IndirectnessAnalyzer indirectnessAnalyzer = new IndirectnessAnalyzer();
        BusinessPhraseDetector businessPhraseDetector = new BusinessPhraseDetector();
        evaluator = new FreeTextEvaluator(keigoValidator, indirectnessAnalyzer, businessPhraseDetector);
    }

    private CulturalScenario createTestScenario() {
        CulturalScenario scenario = CulturalScenario.create(
                "Test", "テスト", "Test situation", "テスト状況",
                WorkplaceContext.EMAIL, RelationshipType.TO_CLIENT,
                ScenarioMode.FREE_TEXT, ScenarioCategory.REPORTING,
                KeigoLevel.KENJOUGO, JapaneseLevel.N2, 75);
        scenario.setKeyPhrases(List.of("お世話になっております", "ご確認"));
        scenario.setAvoidPhrases(List.of("すみません"));
        scenario.setModelAnswer("お世話になっております。ご確認お願いいたします。");
        return scenario;
    }

    @Test
    @DisplayName("should return score combining all dimensions")
    void shouldCombineAllDimensions() {
        CulturalScenario scenario = createTestScenario();
        ScenarioEvaluator.EvaluationResult result = evaluator.evaluate(
                scenario, "お世話になっております。ご確認お願いいたします。", null);

        assertThat(result.score().keigoScore()).isGreaterThan(0);
        assertThat(result.score().appropriatenessScore()).isGreaterThan(0);
        assertThat(result.score().indirectnessScore()).isGreaterThan(0);
        assertThat(result.score().professionalToneScore()).isGreaterThan(0);
        assertThat(result.score().overallScore()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should increase appropriateness for key phrases found")
    void shouldIncreaseForKeyPhrases() {
        CulturalScenario scenario = createTestScenario();
        ScenarioEvaluator.EvaluationResult withPhrases = evaluator.evaluate(
                scenario, "お世話になっております。ご確認いたします。", null);
        ScenarioEvaluator.EvaluationResult withoutPhrases = evaluator.evaluate(
                scenario, "こんにちは。確認します。", null);

        assertThat(withPhrases.score().appropriatenessScore())
                .isGreaterThan(withoutPhrases.score().appropriatenessScore());
    }

    @Test
    @DisplayName("should decrease appropriateness for avoid phrases")
    void shouldDecreaseForAvoidPhrases() {
        CulturalScenario scenario = createTestScenario();
        ScenarioEvaluator.EvaluationResult withAvoid = evaluator.evaluate(
                scenario, "すみません、確認します", null);
        ScenarioEvaluator.EvaluationResult withoutAvoid = evaluator.evaluate(
                scenario, "お世話になっております。確認いたします", null);

        assertThat(withAvoid.score().appropriatenessScore())
                .isLessThan(withoutAvoid.score().appropriatenessScore());
    }

    @Test
    @DisplayName("should detect keigo violations in response")
    void shouldDetectKeigoViolations() {
        CulturalScenario scenario = createTestScenario();
        ScenarioEvaluator.EvaluationResult result = evaluator.evaluate(
                scenario, "俺が確認するよ", null);

        assertThat(result.violations()).isNotEmpty();
        assertThat(result.score().keigoScore()).isLessThan(100);
    }

    @Test
    @DisplayName("should handle null model answer gracefully")
    void shouldHandleNullModelAnswer() {
        CulturalScenario scenario = createTestScenario();
        scenario.setModelAnswer(null);

        ScenarioEvaluator.EvaluationResult result = evaluator.evaluate(scenario, "何かの回答です", null);
        assertThat(result.score()).isNotNull();
        assertThat(result.feedback()).doesNotContain("null");
    }
}
