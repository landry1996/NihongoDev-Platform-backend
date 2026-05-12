package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.domain.model.ReviewLevel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CushionWordDetectorTest {

    private final CushionWordDetector detector = new CushionWordDetector();

    @Test
    void countCushionWords_withMultiple_shouldCountAll() {
        String text = "恐縮ですが、念のため確認させてください";
        assertThat(detector.countCushionWords(text)).isEqualTo(2);
    }

    @Test
    void countCushionWords_withNone_shouldReturnZero() {
        String text = "ここを修正してください";
        assertThat(detector.countCushionWords(text)).isEqualTo(0);
    }

    @Test
    void scoreCushionUsage_suggestion_withCushion_shouldScoreHigh() {
        String text = "恐縮ですが、もし可能であれば修正をお願いします";
        int score = detector.scoreCushionUsage(text, ReviewLevel.SUGGESTION);
        assertThat(score).isEqualTo(100);
    }

    @Test
    void scoreCushionUsage_suggestion_withoutCushion_shouldScoreLow() {
        String text = "修正してください";
        int score = detector.scoreCushionUsage(text, ReviewLevel.SUGGESTION);
        assertThat(score).isEqualTo(30);
    }
}
