package com.nihongodev.platform.application.service.codejapanese.evaluators;

import com.nihongodev.platform.application.service.codejapanese.CodeExerciseEvaluator;
import com.nihongodev.platform.application.service.codejapanese.CushionWordDetector;
import com.nihongodev.platform.application.service.codejapanese.TechnicalTermDetector;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TechDiscussionEvaluator implements CodeExerciseEvaluator {

    private final TechnicalTermDetector termDetector;
    private final CushionWordDetector cushionDetector;

    public TechDiscussionEvaluator(TechnicalTermDetector termDetector, CushionWordDetector cushionDetector) {
        this.termDetector = termDetector;
        this.cushionDetector = cushionDetector;
    }

    @Override
    public CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse) {
        int technical = evaluateTechnicalContent(exercise, userResponse);
        int japanese = termDetector.evaluateTermUsage(userResponse, exercise.getCodeContext());
        int professional = evaluateProfessionalTone(userResponse);
        int structure = evaluateStructure(userResponse);
        int teamComm = evaluateTeamCommunication(userResponse);

        CodeExerciseScore score = CodeExerciseScore.calculate(technical, japanese, professional, structure, teamComm);
        List<TechnicalJapaneseViolation> violations = collectViolations(exercise, userResponse);
        String feedback = generateFeedback(score);

        return new CodeExerciseEvaluationResult(score, violations, null, feedback);
    }

    private int evaluateTechnicalContent(CodeReviewExercise exercise, String response) {
        int score = 50;
        if (exercise.getKeyPhrases() != null) {
            for (String phrase : exercise.getKeyPhrases()) {
                if (response.contains(phrase)) score += 12;
            }
        }
        return Math.min(100, score);
    }

    private int evaluateProfessionalTone(String response) {
        int score = 50;
        if (response.contains("いかがでしょうか") || response.contains("でしょうか")) score += 20;
        if (response.contains("と考えています") || response.contains("と思います")) score += 15;
        if (response.contains("やるべき") || response.contains("絶対")) score -= 15;
        return Math.max(0, Math.min(100, score));
    }

    private int evaluateStructure(String response) {
        int score = 50;
        if (response.length() >= 50) score += 15;
        if (response.contains("。") && response.split("。").length >= 2) score += 15;
        return Math.min(100, score);
    }

    private int evaluateTeamCommunication(String response) {
        int score = 50;
        int cushions = cushionDetector.countCushionWords(response);
        score += cushions * 15;
        if (response.contains("いかがでしょうか")) score += 15;
        return Math.min(100, score);
    }

    private List<TechnicalJapaneseViolation> collectViolations(CodeReviewExercise exercise, String response) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();
        if (exercise.getAvoidPhrases() != null) {
            for (String avoid : exercise.getAvoidPhrases()) {
                String phrase = avoid.replaceAll("\\s*\\(.*\\)", "").trim();
                if (response.contains(phrase)) {
                    violations.add(new TechnicalJapaneseViolation(
                        phrase, "この表現は技術的議論では不適切です",
                        ViolationType.TOO_DIRECT, "Avoid phrase in tech discussion", Severity.MODERATE
                    ));
                }
            }
        }
        return violations;
    }

    private String generateFeedback(CodeExerciseScore score) {
        if (score.overallScore() >= 80) return "プロフェッショナルな技術的提案です！丁寧な表現で分かりやすいです。";
        if (score.overallScore() >= 60) return "良い提案ですが、もう少し丁寧な表現を使うとより効果的です。";
        return "技術的議論では、提案形式（いかがでしょうか）を使い、データで裏付けてください。";
    }
}
