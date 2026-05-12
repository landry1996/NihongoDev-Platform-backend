package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.domain.model.ReviewLevel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewToneAnalyzerTest {

    private final ReviewToneAnalyzer analyzer = new ReviewToneAnalyzer();

    @Test
    void evaluate_withCushionWordAndQuestionForm_shouldScoreHigh() {
        String response = "恐縮ですが、この部分はいかがでしょうか";
        int score = analyzer.evaluate(response, ReviewLevel.SUGGESTION);
        assertThat(score).isGreaterThanOrEqualTo(70);
    }

    @Test
    void evaluate_withBareImperative_shouldScoreLow() {
        String response = "直せ。これは間違いだ。";
        int score = analyzer.evaluate(response, ReviewLevel.SUGGESTION);
        assertThat(score).isLessThanOrEqualTo(40);
    }

    @Test
    void evaluate_tooCasualLanguage_shouldPenalize() {
        String response = "これダメだよ、バグってる";
        int score = analyzer.evaluate(response, ReviewLevel.SUGGESTION);
        assertThat(score).isLessThan(50);
    }

    @Test
    void evaluate_mustFixWithCorrection_shouldBonus() {
        String response = "セキュリティの修正が必要です";
        int score = analyzer.evaluate(response, ReviewLevel.MUST_FIX);
        assertThat(score).isGreaterThanOrEqualTo(55);
    }

    @Test
    void evaluate_withReasonPattern_shouldBonus() {
        String response = "パフォーマンスのため、バッチ取得はいかがでしょうか";
        int score = analyzer.evaluate(response, ReviewLevel.SUGGESTION);
        assertThat(score).isGreaterThanOrEqualTo(65);
    }
}
