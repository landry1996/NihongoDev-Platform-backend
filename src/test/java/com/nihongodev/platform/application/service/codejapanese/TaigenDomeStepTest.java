package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.application.service.codejapanese.steps.TaigenDomeStep;
import com.nihongodev.platform.domain.model.CommitMessageRule;
import com.nihongodev.platform.domain.model.CommitPrefix;
import com.nihongodev.platform.domain.model.TechnicalJapaneseViolation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TaigenDomeStepTest {

    private final TaigenDomeStep step = new TaigenDomeStep();

    private CommitMessageRule ruleRequiring() {
        return new CommitMessageRule(CommitPrefix.FEAT, true, 72, false, null, List.of(), List.of(), List.of());
    }

    @Test
    void isTaigenDome_endingWithNoun_shouldReturnTrue() {
        assertThat(step.isTaigenDome("feat(auth): ユーザー登録機能の追加")).isTrue();
    }

    @Test
    void isTaigenDome_endingWithVerb_shouldReturnFalse() {
        assertThat(step.isTaigenDome("feat(auth): ユーザー登録機能を追加しました")).isFalse();
    }

    @Test
    void isTaigenDome_endingWithParenthetical_shouldCheckBeforeParen() {
        assertThat(step.isTaigenDome("feat(auth): バグの修正（#123対応）")).isTrue();
    }

    @Test
    void analyze_whenRequired_andVerbEnding_shouldReturnViolation() {
        List<TechnicalJapaneseViolation> violations = step.analyze("feat(auth): 機能を追加しました", ruleRequiring());
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).violationType()).isEqualTo(com.nihongodev.platform.domain.model.ViolationType.VERB_ENDING_IN_COMMIT);
    }

    @Test
    void analyze_whenNotRequired_shouldReturnEmpty() {
        CommitMessageRule rule = new CommitMessageRule(CommitPrefix.FEAT, false, 72, false, null, List.of(), List.of(), List.of());
        List<TechnicalJapaneseViolation> violations = step.analyze("feat: 何かしました", rule);
        assertThat(violations).isEmpty();
    }
}
