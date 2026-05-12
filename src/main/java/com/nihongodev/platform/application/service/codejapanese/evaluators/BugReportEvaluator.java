package com.nihongodev.platform.application.service.codejapanese.evaluators;

import com.nihongodev.platform.application.service.codejapanese.CodeExerciseEvaluator;
import com.nihongodev.platform.application.service.codejapanese.TechnicalTermDetector;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BugReportEvaluator implements CodeExerciseEvaluator {

    private static final List<String> REQUIRED_SECTIONS = List.of(
        "概要", "再現手順", "期待結果", "実際の結果"
    );

    private final TechnicalTermDetector termDetector;

    public BugReportEvaluator(TechnicalTermDetector termDetector) {
        this.termDetector = termDetector;
    }

    @Override
    public CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse) {
        int structure = evaluateStructure(userResponse);
        int technical = evaluateTechnicalContent(exercise, userResponse);
        int japanese = termDetector.evaluateTermUsage(userResponse, exercise.getCodeContext());
        int professional = evaluateProfessionalTone(userResponse);
        int teamComm = evaluateClarity(userResponse);

        CodeExerciseScore score = CodeExerciseScore.calculate(technical, japanese, professional, structure, teamComm);
        List<TechnicalJapaneseViolation> violations = collectViolations(userResponse);
        String feedback = generateFeedback(score, structure);

        return new CodeExerciseEvaluationResult(score, violations, null, feedback);
    }

    private int evaluateStructure(String response) {
        int sectionsFound = 0;
        for (String section : REQUIRED_SECTIONS) {
            if (response.contains(section)) sectionsFound++;
        }
        return (sectionsFound * 100) / REQUIRED_SECTIONS.size();
    }

    private int evaluateTechnicalContent(CodeReviewExercise exercise, String response) {
        int score = 50;
        if (exercise.getTechnicalIssues() != null) {
            for (String issue : exercise.getTechnicalIssues()) {
                if (response.toLowerCase().contains(issue.toLowerCase())) score += 15;
            }
        }
        if (response.contains("環境") || response.contains("バージョン")) score += 10;
        return Math.min(100, score);
    }

    private int evaluateProfessionalTone(String response) {
        int score = 60;
        if (response.contains("1.") || response.contains("1、")) score += 15;
        if (response.split("\n").length >= 5) score += 15;
        return Math.min(100, score);
    }

    private int evaluateClarity(String response) {
        int score = 60;
        if (response.length() >= 100) score += 15;
        if (response.contains("##") || response.contains("■")) score += 15;
        return Math.min(100, score);
    }

    private List<TechnicalJapaneseViolation> collectViolations(String response) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();
        for (String section : REQUIRED_SECTIONS) {
            if (!response.contains(section)) {
                violations.add(new TechnicalJapaneseViolation(
                    "Missing: " + section, section + " セクションを追加してください",
                    ViolationType.WRONG_STRUCTURE, "Required bug report section missing", Severity.MODERATE
                ));
            }
        }
        return violations;
    }

    private String generateFeedback(CodeExerciseScore score, int structureScore) {
        if (score.overallScore() >= 80) return "構造化されたバグレポートです。再現手順が明確で分かりやすいです。";
        if (structureScore < 50) return "バグレポートには概要、再現手順、期待結果、実際の結果のセクションが必要です。";
        return "バグレポートの内容をもう少し具体的にしてください。";
    }
}
