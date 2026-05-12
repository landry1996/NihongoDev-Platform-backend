package com.nihongodev.platform.application.service.codejapanese.evaluators;

import com.nihongodev.platform.application.service.codejapanese.CodeExerciseEvaluator;
import com.nihongodev.platform.application.service.codejapanese.CommitMessageValidator;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommitMessageEvaluator implements CodeExerciseEvaluator {

    private final CommitMessageValidator commitValidator;

    public CommitMessageEvaluator(CommitMessageValidator commitValidator) {
        this.commitValidator = commitValidator;
    }

    @Override
    public CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse) {
        CommitMessageRule rule = exercise.getCommitRule();
        if (rule == null) {
            rule = new CommitMessageRule(CommitPrefix.FEAT, true, 72, false, null, List.of(), List.of(), List.of());
        }

        CommitMessageAnalysis analysis = commitValidator.validate(userResponse, rule);

        int technical = analysis.commitScore();
        int japanese = evaluateJapaneseQuality(exercise, userResponse);
        int professional = analysis.isTaigenDome() ? 80 : 40;
        int structure = evaluateStructure(analysis);
        int teamComm = 70;

        CodeExerciseScore score = CodeExerciseScore.calculate(technical, japanese, professional, structure, teamComm);
        List<TechnicalJapaneseViolation> violations = collectViolations(analysis, rule);
        String feedback = generateFeedback(score, analysis);

        return new CodeExerciseEvaluationResult(score, violations, analysis, feedback);
    }

    private int evaluateJapaneseQuality(CodeReviewExercise exercise, String response) {
        int score = 60;
        if (exercise.getKeyPhrases() != null) {
            for (String phrase : exercise.getKeyPhrases()) {
                if (response.contains(phrase)) score += 15;
            }
        }
        return Math.min(100, score);
    }

    private int evaluateStructure(CommitMessageAnalysis analysis) {
        int score = 50;
        if (analysis.hasValidPrefix()) score += 20;
        if (analysis.hasScope()) score += 15;
        if (analysis.isWithinMaxLength()) score += 15;
        return Math.min(100, score);
    }

    private List<TechnicalJapaneseViolation> collectViolations(CommitMessageAnalysis analysis, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = new java.util.ArrayList<>();
        if (!analysis.hasValidPrefix()) {
            violations.add(new TechnicalJapaneseViolation(
                "prefix missing", rule.expectedPrefix().getEnglish() + " プレフィックスが必要です",
                ViolationType.WRONG_STRUCTURE, "Missing commit prefix", Severity.CRITICAL
            ));
        }
        if (rule.requireTaigenDome() && !analysis.isTaigenDome()) {
            violations.add(new TechnicalJapaneseViolation(
                "verb ending", "体言止めを使用してください",
                ViolationType.VERB_ENDING_IN_COMMIT, "Must use noun ending", Severity.MODERATE
            ));
        }
        return violations;
    }

    private String generateFeedback(CodeExerciseScore score, CommitMessageAnalysis analysis) {
        if (score.overallScore() >= 80) return "優秀なコミットメッセージです！体言止めと構造が正しいです。";
        if (!analysis.hasValidPrefix()) return "コミットメッセージにプレフィックス（feat/fix/refactor等）を追加してください。";
        if (!analysis.isTaigenDome()) return "体言止め（名詞で終わる形）を使用してください。例：「〜の追加」「〜の修正」";
        return "コミットメッセージの構造を確認してください。";
    }
}
