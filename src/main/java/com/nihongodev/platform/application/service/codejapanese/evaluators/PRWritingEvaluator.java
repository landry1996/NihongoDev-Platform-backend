package com.nihongodev.platform.application.service.codejapanese.evaluators;

import com.nihongodev.platform.application.service.codejapanese.CodeExerciseEvaluator;
import com.nihongodev.platform.application.service.codejapanese.TechnicalTermDetector;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PRWritingEvaluator implements CodeExerciseEvaluator {

    private static final Map<PRSection, String> SECTION_HEADERS = Map.of(
        PRSection.BACKGROUND, "背景",
        PRSection.CHANGES, "変更内容",
        PRSection.IMPACT, "影響範囲",
        PRSection.TEST_PLAN, "テスト計画",
        PRSection.NOTES, "備考",
        PRSection.SCREENSHOTS, "スクリーンショット"
    );

    private final TechnicalTermDetector termDetector;

    public PRWritingEvaluator(TechnicalTermDetector termDetector) {
        this.termDetector = termDetector;
    }

    @Override
    public CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse) {
        int structure = evaluateStructure(exercise, userResponse);
        int technical = evaluateTechnicalContent(exercise, userResponse);
        int japanese = termDetector.evaluateTermUsage(userResponse, exercise.getCodeContext());
        int professional = evaluateProfessionalTone(userResponse);
        int teamComm = evaluateClarity(userResponse);

        CodeExerciseScore score = CodeExerciseScore.calculate(technical, japanese, professional, structure, teamComm);
        List<TechnicalJapaneseViolation> violations = collectViolations(exercise, userResponse);
        String feedback = generateFeedback(score, structure);

        return new CodeExerciseEvaluationResult(score, violations, null, feedback);
    }

    private int evaluateStructure(CodeReviewExercise exercise, String response) {
        int score = 0;
        PRTemplate template = exercise.getPrTemplate();
        if (template == null) {
            return response.contains("##") ? 70 : 40;
        }

        int sectionsFound = 0;
        for (PRSection section : template.requiredSections()) {
            String header = SECTION_HEADERS.get(section);
            if (header != null && response.contains(header)) {
                sectionsFound++;
            }
        }

        if (sectionsFound >= template.minSections()) score = 100;
        else score = (sectionsFound * 100) / template.minSections();

        return Math.min(100, score);
    }

    private int evaluateTechnicalContent(CodeReviewExercise exercise, String response) {
        int score = 50;
        if (exercise.getKeyPhrases() != null) {
            for (String phrase : exercise.getKeyPhrases()) {
                if (response.contains(phrase)) score += 10;
            }
        }
        return Math.min(100, score);
    }

    private int evaluateProfessionalTone(String response) {
        int score = 60;
        if (response.contains("- ") || response.contains("・")) score += 15;
        if (response.contains("##")) score += 10;
        if (response.length() >= 100) score += 10;
        return Math.min(100, score);
    }

    private int evaluateClarity(String response) {
        int score = 60;
        if (response.split("\n").length >= 5) score += 20;
        if (response.contains("確認") || response.contains("テスト")) score += 10;
        return Math.min(100, score);
    }

    private List<TechnicalJapaneseViolation> collectViolations(CodeReviewExercise exercise, String response) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();
        PRTemplate template = exercise.getPrTemplate();
        if (template != null && template.requiresJapaneseOnly()) {
            if (response.contains("Background") || response.contains("Changes") || response.contains("Impact")) {
                violations.add(new TechnicalJapaneseViolation(
                    "English headers", "日本語のヘッダーを使用してください（背景、変更内容、影響範囲）",
                    ViolationType.MIXED_LANGUAGE, "Headers must be in Japanese", Severity.MODERATE
                ));
            }
        }
        if (template != null) {
            for (PRSection section : template.requiredSections()) {
                String header = SECTION_HEADERS.get(section);
                if (header != null && !response.contains(header)) {
                    violations.add(new TechnicalJapaneseViolation(
                        "Missing section", header + " セクションが必要です",
                        ViolationType.WRONG_STRUCTURE, "Required section missing: " + header, Severity.MODERATE
                    ));
                }
            }
        }
        return violations;
    }

    private String generateFeedback(CodeExerciseScore score, int structureScore) {
        if (score.overallScore() >= 80) return "よくできたPR説明文です！構造が明確で読みやすいです。";
        if (structureScore < 50) return "セクション構造を確認してください。背景、変更内容、影響範囲、テスト計画が必要です。";
        return "PR説明文の内容をもう少し充実させてください。";
    }
}
