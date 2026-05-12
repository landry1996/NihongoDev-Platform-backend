package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.application.service.codejapanese.steps.*;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommitMessageValidatorTest {

    private CommitMessageValidator validator;

    @BeforeEach
    void setUp() {
        PrefixStep prefixStep = new PrefixStep();
        ScopeStep scopeStep = new ScopeStep();
        TaigenDomeStep taigenDomeStep = new TaigenDomeStep();
        LengthStep lengthStep = new LengthStep();
        VerbEndingDetectionStep verbEndingStep = new VerbEndingDetectionStep();
        TechnicalTermStep technicalTermStep = new TechnicalTermStep();

        List<CommitAnalysisStep> steps = List.of(prefixStep, scopeStep, taigenDomeStep, lengthStep, verbEndingStep, technicalTermStep);
        validator = new CommitMessageValidator(steps, prefixStep, scopeStep, taigenDomeStep, verbEndingStep);
    }

    private CommitMessageRule defaultRule() {
        return new CommitMessageRule(CommitPrefix.FEAT, true, 72, true, "auth",
            List.of("しました", "します"), List.of(), List.of());
    }

    @Test
    void validate_validCommitMessage_shouldReturnHighScore() {
        CommitMessageAnalysis result = validator.validate("feat(auth): ユーザー登録機能の追加", defaultRule());
        assertThat(result.hasValidPrefix()).isTrue();
        assertThat(result.detectedPrefix()).isEqualTo(CommitPrefix.FEAT);
        assertThat(result.isTaigenDome()).isTrue();
        assertThat(result.hasScope()).isTrue();
        assertThat(result.detectedScope()).isEqualTo("auth");
        assertThat(result.commitScore()).isGreaterThanOrEqualTo(80);
    }

    @Test
    void validate_missingPrefix_shouldDetectError() {
        CommitMessageAnalysis result = validator.validate("ユーザー登録機能の追加", defaultRule());
        assertThat(result.hasValidPrefix()).isFalse();
        assertThat(result.commitScore()).isLessThan(80);
    }

    @Test
    void validate_verbEnding_shouldDetectViolation() {
        CommitMessageAnalysis result = validator.validate("feat(auth): ユーザー登録機能を追加しました", defaultRule());
        assertThat(result.detectedVerbEndings()).contains("しました");
        assertThat(result.isTaigenDome()).isFalse();
    }

    @Test
    void validate_tooLong_shouldPenalize() {
        String longMsg = "feat(auth): " + "あ".repeat(100);
        CommitMessageAnalysis result = validator.validate(longMsg, defaultRule());
        assertThat(result.isWithinMaxLength()).isFalse();
    }

    @Test
    void validate_wrongPrefix_shouldDetect() {
        CommitMessageAnalysis result = validator.validate("fix(auth): ユーザー登録機能の追加", defaultRule());
        assertThat(result.hasValidPrefix()).isTrue();
        assertThat(result.detectedPrefix()).isEqualTo(CommitPrefix.FIX);
    }

    @Test
    void validate_noScope_shouldDetect() {
        CommitMessageAnalysis result = validator.validate("feat: ユーザー登録機能の追加", defaultRule());
        assertThat(result.hasScope()).isFalse();
    }

    @Test
    void validate_correctTaigenDome_withParenthetical() {
        CommitMessageAnalysis result = validator.validate("feat(auth): ユーザー登録機能の追加（バリデーション付き）", defaultRule());
        assertThat(result.isTaigenDome()).isTrue();
    }

    @Test
    void validate_multipleVerbEndings_shouldDetectAll() {
        CommitMessageAnalysis result = validator.validate("feat(auth): 機能を追加した。テストもした", defaultRule());
        assertThat(result.detectedVerbEndings()).isNotEmpty();
    }
}
