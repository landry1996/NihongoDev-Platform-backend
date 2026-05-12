package com.nihongodev.platform.application.service.codejapanese.evaluators;

import com.nihongodev.platform.application.service.codejapanese.*;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CodeReviewEvaluator implements CodeExerciseEvaluator {

    private final ReviewToneAnalyzer toneAnalyzer;
    private final TechnicalTermDetector termDetector;
    private final CushionWordDetector cushionDetector;

    public CodeReviewEvaluator(ReviewToneAnalyzer toneAnalyzer, TechnicalTermDetector termDetector,
                               CushionWordDetector cushionDetector) {
        this.toneAnalyzer = toneAnalyzer;
        this.termDetector = termDetector;
        this.cushionDetector = cushionDetector;
    }

    @Override
    public CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse) {
        int technical = evaluateTechnicalContent(exercise, userResponse);
        int japanese = evaluateJapaneseQuality(exercise, userResponse);
        int professional = toneAnalyzer.evaluate(userResponse, exercise.getExpectedReviewLevel());
        int structure = evaluateStructure(userResponse);
        int teamComm = evaluateTeamCommunication(userResponse);

        CodeExerciseScore score = CodeExerciseScore.calculate(technical, japanese, professional, structure, teamComm);
        List<TechnicalJapaneseViolation> violations = collectViolations(exercise, userResponse);
        String feedback = generateFeedback(score, violations);

        return new CodeExerciseEvaluationResult(score, violations, null, feedback);
    }

    private int evaluateTechnicalContent(CodeReviewExercise exercise, String response) {
        int score = 40;
        if (exercise.getTechnicalIssues() != null) {
            for (String issue : exercise.getTechnicalIssues()) {
                if (response.toLowerCase().contains(issue.toLowerCase())) {
                    score += 20;
                }
            }
        }
        if (exercise.getKeyPhrases() != null) {
            for (String phrase : exercise.getKeyPhrases()) {
                if (response.contains(phrase)) {
                    score += 10;
                }
            }
        }
        return Math.min(100, score);
    }

    private int evaluateJapaneseQuality(CodeReviewExercise exercise, String response) {
        int score = termDetector.evaluateTermUsage(response, exercise.getCodeContext());
        if (exercise.getTechnicalTermsJp() != null) {
            for (String term : exercise.getTechnicalTermsJp()) {
                if (response.contains(term)) {
                    score += 5;
                }
            }
        }
        return Math.min(100, score);
    }

    private int evaluateStructure(String response) {
        int score = 50;
        if (response.length() >= 30) score += 15;
        if (response.contains("。")) score += 10;
        if (response.length() >= 50 && response.length() <= 500) score += 15;
        return Math.min(100, score);
    }

    private int evaluateTeamCommunication(String response) {
        int score = 50;
        if (response.contains("いかがでしょうか") || response.contains("ご検討")) score += 20;
        if (response.contains("かもしれません") || response.contains("と思います")) score += 15;
        if (response.length() < 10) score -= 20;
        return Math.max(0, Math.min(100, score));
    }

    private List<TechnicalJapaneseViolation> collectViolations(CodeReviewExercise exercise, String response) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();
        if (exercise.getAvoidPhrases() != null) {
            for (String avoid : exercise.getAvoidPhrases()) {
                String phrase = avoid.replaceAll("\\s*\\(.*\\)", "").trim();
                if (response.contains(phrase)) {
                    violations.add(new TechnicalJapaneseViolation(
                        phrase, "この表現は避けてください", ViolationType.TOO_DIRECT,
                        "Avoid phrase detected", Severity.MODERATE
                    ));
                }
            }
        }
        if (cushionDetector.countCushionWords(response) == 0 &&
            exercise.getExpectedReviewLevel() == ReviewLevel.SUGGESTION) {
            violations.add(new TechnicalJapaneseViolation(
                response.substring(0, Math.min(20, response.length())),
                "クッション言葉を使用してください（例：細かい点ですが）",
                ViolationType.MISSING_CUSHION_WORD,
                "Missing cushion word for suggestion-level review",
                Severity.MODERATE
            ));
        }
        return violations;
    }

    private String generateFeedback(CodeExerciseScore score, List<TechnicalJapaneseViolation> violations) {
        StringBuilder fb = new StringBuilder();
        if (score.overallScore() >= 80) {
            fb.append("素晴らしいレビューコメントです！");
        } else if (score.overallScore() >= 60) {
            fb.append("良いレビューコメントですが、改善点があります。");
        } else {
            fb.append("レビューコメントの改善が必要です。");
        }
        if (!violations.isEmpty()) {
            fb.append(" 指摘事項: ").append(violations.size()).append("件");
        }
        return fb.toString();
    }
}
