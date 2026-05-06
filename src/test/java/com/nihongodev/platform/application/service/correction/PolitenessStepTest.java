package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PolitenessStep")
class PolitenessStepTest {

    private PolitenessStep step;

    @BeforeEach
    void setUp() {
        step = new PolitenessStep();
    }

    @Test
    @DisplayName("should score high for keigo in strict context")
    void shouldScoreHighForKeigo() {
        CorrectionContext context = CorrectionContext.of(TextType.EMAIL_TO_CLIENT, JapaneseLevel.N3);
        String text = "お世話になっております。ご確認いただけますでしょうか。よろしくお願いいたします。";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.score().getPolitenessScore()).isGreaterThanOrEqualTo(70);
    }

    @Test
    @DisplayName("should penalize casual endings in strict keigo context")
    void shouldPenalizeCasualInStrictContext() {
        CorrectionContext context = CorrectionContext.of(TextType.EMAIL_TO_CLIENT, JapaneseLevel.N3);
        String text = "これだよ明日やるじゃん";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.annotations()).isNotEmpty();
        assertThat(result.annotations().stream()
                .anyMatch(a -> a.getSeverity() == Severity.ERROR)).isTrue();
    }

    @Test
    @DisplayName("should be lenient with casual in slack context")
    void shouldBeLenientInCasualContext() {
        CorrectionContext context = CorrectionContext.of(TextType.SLACK_MESSAGE, JapaneseLevel.N3);
        String text = "これだよ了解";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        boolean hasError = result.annotations().stream()
                .anyMatch(a -> a.getSeverity() == Severity.ERROR);
        assertThat(hasError).isFalse();
    }

    @Test
    @DisplayName("should warn when keigo is missing in formal context")
    void shouldWarnMissingKeigo() {
        CorrectionContext context = CorrectionContext.of(TextType.EMAIL_TO_CLIENT, JapaneseLevel.N3);
        String text = "明日のミーティングで話す。新しい機能の進捗を報告する予定だ。確認してほしい。";

        CorrectionStep.CorrectionStepResult result = step.process(text, context);

        assertThat(result.annotations().stream()
                .anyMatch(a -> a.getCategory() == AnnotationCategory.KEIGO)).isTrue();
    }

    @Test
    @DisplayName("should reward desu/masu usage")
    void shouldRewardDesuMasu() {
        CorrectionContext context = CorrectionContext.of(TextType.STANDUP_REPORT, JapaneseLevel.N4);
        String textFormal = "完了しました。明日も続けます。";
        String textCasual = "終わった。明日もやる。";

        CorrectionStep.CorrectionStepResult formalResult = step.process(textFormal, context);
        CorrectionStep.CorrectionStepResult casualResult = step.process(textCasual, context);

        assertThat(formalResult.score().getPolitenessScore())
                .isGreaterThan(casualResult.score().getPolitenessScore());
    }
}
