package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.domain.model.WorkplaceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IndirectnessAnalyzer")
class IndirectnessAnalyzerTest {

    private final IndirectnessAnalyzer analyzer = new IndirectnessAnalyzer();

    @Test
    @DisplayName("should award bonus for cushion words")
    void shouldAwardBonusForCushionWords() {
        int score = analyzer.score("恐れ入りますが、確認させていただきます", WorkplaceContext.EMAIL);
        assertThat(score).isGreaterThan(50);
    }

    @Test
    @DisplayName("should award bonus for hedging expressions")
    void shouldAwardBonusForHedging() {
        int score = analyzer.score("そうかもしれませんが、検討いたします", WorkplaceContext.MEETING);
        assertThat(score).isGreaterThan(50);
    }

    @Test
    @DisplayName("should penalize direct refusal")
    void shouldPenalizeDirectRefusal() {
        int score = analyzer.score("できません。無理です。", WorkplaceContext.MEETING);
        assertThat(score).isLessThan(50);
    }

    @Test
    @DisplayName("should penalize blunt assertion")
    void shouldPenalizeBluntAssertion() {
        int score = analyzer.score("やってください。すぐにやります。", WorkplaceContext.MEETING);
        assertThat(score).isLessThan(50);
    }

    @Test
    @DisplayName("should return base score for neutral text")
    void shouldReturnBaseForNeutralText() {
        int score = analyzer.score("本日の天気は晴れです", WorkplaceContext.STANDUP);
        assertThat(score).isBetween(40, 60);
    }
}
