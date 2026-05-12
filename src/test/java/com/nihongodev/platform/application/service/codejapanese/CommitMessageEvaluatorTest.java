package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.application.service.codejapanese.evaluators.CommitMessageEvaluator;
import com.nihongodev.platform.application.service.codejapanese.steps.*;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommitMessageEvaluatorTest {

    private CommitMessageEvaluator evaluator;

    @BeforeEach
    void setUp() {
        PrefixStep prefixStep = new PrefixStep();
        ScopeStep scopeStep = new ScopeStep();
        TaigenDomeStep taigenDomeStep = new TaigenDomeStep();
        LengthStep lengthStep = new LengthStep();
        VerbEndingDetectionStep verbEndingStep = new VerbEndingDetectionStep();
        TechnicalTermStep technicalTermStep = new TechnicalTermStep();
        List<CommitAnalysisStep> steps = List.of(prefixStep, scopeStep, taigenDomeStep, lengthStep, verbEndingStep, technicalTermStep);
        CommitMessageValidator validator = new CommitMessageValidator(steps, prefixStep, scopeStep, taigenDomeStep, verbEndingStep);
        evaluator = new CommitMessageEvaluator(validator);
    }

    private CodeReviewExercise createCommitExercise() {
        CodeReviewExercise exercise = new CodeReviewExercise();
        exercise.setType(ExerciseType.COMMIT_MESSAGE);
        exercise.setCodeContext(CodeContext.BACKEND_JAVA);
        exercise.setCommitRule(new CommitMessageRule(
            CommitPrefix.FEAT, true, 72, true, "auth",
            List.of("しました", "します"), List.of(), List.of()
        ));
        exercise.setKeyPhrases(List.of("追加", "機能"));
        return exercise;
    }

    @Test
    void evaluate_validCommit_shouldScoreHigh() {
        var result = evaluator.evaluate(createCommitExercise(), "feat(auth): ユーザー登録機能の追加");
        assertThat(result.score().overallScore()).isGreaterThanOrEqualTo(60);
        assertThat(result.commitAnalysis()).isNotNull();
        assertThat(result.commitAnalysis().hasValidPrefix()).isTrue();
    }

    @Test
    void evaluate_verbEndingCommit_shouldScoreLow() {
        var result = evaluator.evaluate(createCommitExercise(), "feat(auth): ユーザー登録機能を追加しました");
        assertThat(result.commitAnalysis().isTaigenDome()).isFalse();
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void evaluate_missingPrefix_shouldDetect() {
        var result = evaluator.evaluate(createCommitExercise(), "ユーザー登録機能の追加");
        assertThat(result.commitAnalysis().hasValidPrefix()).isFalse();
        assertThat(result.violations().stream()
            .anyMatch(v -> v.violationType() == ViolationType.WRONG_STRUCTURE)).isTrue();
    }

    @Test
    void evaluate_shouldReturnCommitAnalysis() {
        var result = evaluator.evaluate(createCommitExercise(), "feat(auth): 認証機能の実装");
        assertThat(result.commitAnalysis()).isNotNull();
        assertThat(result.commitAnalysis().detectedScope()).isEqualTo("auth");
    }

    @Test
    void evaluate_shouldGenerateFeedback() {
        var result = evaluator.evaluate(createCommitExercise(), "feat(auth): ユーザー登録機能の追加");
        assertThat(result.feedback()).isNotBlank();
    }
}
