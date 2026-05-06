package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GrammarStep")
class GrammarStepTest {

    private GrammarStep step;
    private CorrectionContext context;

    @BeforeEach
    void setUp() {
        step = new GrammarStep();
        context = CorrectionContext.of(TextType.FREE_TEXT, JapaneseLevel.N3);
    }

    @Test
    @DisplayName("should detect grammar error: についてを")
    void shouldDetectRedundantParticle() {
        String text = "このプロジェクトについてを説明します";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.annotations()).isNotEmpty();
        assertThat(result.annotations().get(0).getCategory()).isEqualTo(AnnotationCategory.GRAMMAR);
        assertThat(result.annotations().get(0).getSeverity()).isEqualTo(Severity.ERROR);
    }

    @Test
    @DisplayName("should detect grammar error: のでから")
    void shouldDetectDoubleCausal() {
        String text = "忙しいのでからできません";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.annotations()).isNotEmpty();
        assertThat(result.annotations().stream()
                .anyMatch(a -> a.getSeverity() == Severity.ERROR)).isTrue();
    }

    @Test
    @DisplayName("should give high score for clean text")
    void shouldGiveHighScoreForCleanText() {
        String text = "本日のスタンドアップで報告します。実装は順調に進んでいます。";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.score().getGrammarScore()).isGreaterThanOrEqualTo(90);
        assertThat(result.annotations()).isEmpty();
    }

    @Test
    @DisplayName("should reduce score for each error found")
    void shouldReduceScorePerError() {
        String textClean = "これは正しい文です。";
        String textWithError = "このについてを問題のでからです";

        CorrectionStep.CorrectionStepResult cleanResult = step.process(textClean, context);
        CorrectionStep.CorrectionStepResult errorResult = step.process(textWithError, context);

        assertThat(cleanResult.score().getGrammarScore())
                .isGreaterThan(errorResult.score().getGrammarScore());
    }

    @Test
    @DisplayName("should detect する事 pattern")
    void shouldDetectKanjiNominalizer() {
        String text = "これをする事が大切です";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.annotations()).isNotEmpty();
        assertThat(result.annotations().stream()
                .anyMatch(a -> a.getSeverity() == Severity.INFO)).isTrue();
    }

    @Test
    @DisplayName("should handle text without Japanese")
    void shouldHandleNonJapaneseText() {
        String text = "Hello world this is a test";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.score().getGrammarScore()).isGreaterThan(0);
    }
}
