package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.application.service.codejapanese.evaluators.CodeReviewEvaluator;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeReviewEvaluatorTest {

    private CodeReviewEvaluator evaluator;

    @BeforeEach
    void setUp() {
        ReviewToneAnalyzer toneAnalyzer = new ReviewToneAnalyzer();
        TechnicalTermDetector termDetector = new TechnicalTermDetector();
        CushionWordDetector cushionDetector = new CushionWordDetector();
        evaluator = new CodeReviewEvaluator(toneAnalyzer, termDetector, cushionDetector);
    }

    private CodeReviewExercise createExercise() {
        CodeReviewExercise exercise = new CodeReviewExercise();
        exercise.setType(ExerciseType.CODE_REVIEW);
        exercise.setCodeContext(CodeContext.BACKEND_JAVA);
        exercise.setExpectedReviewLevel(ReviewLevel.SUGGESTION);
        exercise.setTechnicalIssues(List.of("null check", "Optional misuse"));
        exercise.setKeyPhrases(List.of("不要かと思います", "シンプルに書ける"));
        exercise.setAvoidPhrases(List.of("ダメです"));
        exercise.setTechnicalTermsJp(List.of("nullチェック", "戻り値"));
        return exercise;
    }

    @Test
    void evaluate_highQualityResponse_shouldScoreHigh() {
        String response = "細かい点ですが、nullチェックは不要かと思います。orElseを使うとよりシンプルに書けるかもしれません。ご検討いただけると幸いです。";
        var result = evaluator.evaluate(createExercise(), response);
        assertThat(result.score().overallScore()).isGreaterThanOrEqualTo(50);
    }

    @Test
    void evaluate_withAvoidPhrase_shouldDetectViolation() {
        String response = "ダメです。nullチェックは不要です。";
        var result = evaluator.evaluate(createExercise(), response);
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void evaluate_missingCushionWord_shouldDetectViolation() {
        String response = "nullチェックは不要です。orElseを使ってください。";
        var result = evaluator.evaluate(createExercise(), response);
        assertThat(result.violations().stream()
            .anyMatch(v -> v.violationType() == ViolationType.MISSING_CUSHION_WORD)).isTrue();
    }

    @Test
    void evaluate_tooShortResponse_shouldScoreLow() {
        String response = "修正";
        var result = evaluator.evaluate(createExercise(), response);
        assertThat(result.score().overallScore()).isLessThan(60);
    }

    @Test
    void evaluate_shouldProvideFeedback() {
        String response = "恐縮ですが、nullチェックは不要かと思います。";
        var result = evaluator.evaluate(createExercise(), response);
        assertThat(result.feedback()).isNotBlank();
    }
}
