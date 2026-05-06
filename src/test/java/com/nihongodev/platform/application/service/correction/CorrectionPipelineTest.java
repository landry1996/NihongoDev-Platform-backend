package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CorrectionPipeline")
class CorrectionPipelineTest {

    private CorrectionPipeline pipeline;

    @BeforeEach
    void setUp() {
        List<CorrectionStep> steps = List.of(
                new GrammarStep(),
                new VocabularyStep(),
                new PolitenessStep(),
                new ClarityStep(),
                new NaturalnessStep(),
                new ProfessionalStep()
        );
        pipeline = new CorrectionPipeline(steps, new PatternCorrectionEngine());
    }

    @Test
    @DisplayName("should execute all steps and produce scores for all 6 dimensions")
    void shouldExecuteAllSteps() {
        CorrectionContext context = CorrectionContext.of(TextType.STANDUP_REPORT, JapaneseLevel.N3);
        String text = "本日は実装を完了しました。テストも確認しました。明日はレビューを対応します。";

        CorrectionPipeline.CorrectionPipelineResult result = pipeline.execute(text, context, List.of());

        assertThat(result.score()).isNotNull();
        assertThat(result.score().getGrammarScore()).isGreaterThanOrEqualTo(0);
        assertThat(result.score().getVocabularyScore()).isGreaterThanOrEqualTo(0);
        assertThat(result.score().getPolitenessScore()).isGreaterThanOrEqualTo(0);
        assertThat(result.score().getClarityScore()).isGreaterThanOrEqualTo(0);
        assertThat(result.score().getNaturalnessScore()).isGreaterThanOrEqualTo(0);
        assertThat(result.score().getProfessionalScore()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("should produce annotations from steps")
    void shouldProduceAnnotations() {
        CorrectionContext context = CorrectionContext.of(TextType.EMAIL_TO_CLIENT, JapaneseLevel.N3);
        String text = "だよこれについてをやる";

        CorrectionPipeline.CorrectionPipelineResult result = pipeline.execute(text, context, List.of());

        assertThat(result.annotations()).isNotEmpty();
    }

    @Test
    @DisplayName("should apply external rules from rule engine")
    void shouldApplyExternalRules() {
        CorrectionContext context = CorrectionContext.of(TextType.FREE_TEXT, JapaneseLevel.N3);
        CorrectionRule rule = CorrectionRule.create("test-rule", AnnotationCategory.GRAMMAR,
                Severity.WARNING, "テスト", "テストです", "Add です", List.of(), JapaneseLevel.N5);

        CorrectionPipeline.CorrectionPipelineResult result = pipeline.execute(
                "これはテストです", context, List.of(rule));

        boolean hasRuleAnnotation = result.annotations().stream()
                .anyMatch(a -> a.getRuleId().equals(rule.getId().toString()));
        assertThat(hasRuleAnnotation).isTrue();
    }

    @Test
    @DisplayName("should merge duplicate annotations")
    void shouldMergeDuplicates() {
        CorrectionContext context = CorrectionContext.of(TextType.FREE_TEXT, JapaneseLevel.N3);
        String text = "簡単なテストです。";

        CorrectionPipeline.CorrectionPipelineResult result = pipeline.execute(text, context, List.of());

        long uniqueAnnotations = result.annotations().stream()
                .map(a -> a.getStartOffset() + "-" + a.getEndOffset() + "-" + a.getRuleId())
                .distinct().count();
        assertThat(result.annotations().size()).isEqualTo((int) uniqueAnnotations);
    }

    @Test
    @DisplayName("should handle empty text gracefully")
    void shouldHandleEmptyText() {
        CorrectionContext context = CorrectionContext.of(TextType.FREE_TEXT, JapaneseLevel.N3);

        CorrectionPipeline.CorrectionPipelineResult result = pipeline.execute("", context, List.of());

        assertThat(result.score()).isNotNull();
        assertThat(result.annotations()).isNotNull();
    }
}
