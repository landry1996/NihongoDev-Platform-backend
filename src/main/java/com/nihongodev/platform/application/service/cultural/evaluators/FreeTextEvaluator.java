package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.application.service.cultural.KeigoValidationResult;
import com.nihongodev.platform.application.service.cultural.KeigoValidator;
import com.nihongodev.platform.application.service.cultural.ScenarioEvaluator;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FreeTextEvaluator implements ScenarioEvaluator {

    private final KeigoValidator keigoValidator;
    private final IndirectnessAnalyzer indirectnessAnalyzer;
    private final BusinessPhraseDetector businessPhraseDetector;

    public FreeTextEvaluator(KeigoValidator keigoValidator,
                             IndirectnessAnalyzer indirectnessAnalyzer,
                             BusinessPhraseDetector businessPhraseDetector) {
        this.keigoValidator = keigoValidator;
        this.indirectnessAnalyzer = indirectnessAnalyzer;
        this.businessPhraseDetector = businessPhraseDetector;
    }

    @Override
    public EvaluationResult evaluate(CulturalScenario scenario, String userResponse, UUID selectedChoiceId) {
        if (userResponse == null || userResponse.isBlank()) {
            return new EvaluationResult(CulturalScore.zero(), List.of(), "No response provided.");
        }

        KeigoValidationResult keigoResult = keigoValidator.validate(
                userResponse, scenario.getExpectedKeigoLevel(), scenario.getRelationship());

        int appropriateness = evaluateAppropriateness(scenario, userResponse);
        int uchiSoto = evaluateUchiSoto(scenario, userResponse);
        int indirectness = indirectnessAnalyzer.score(userResponse, scenario.getContext());
        int professional = businessPhraseDetector.score(userResponse, scenario.getContext());

        CulturalScore score = CulturalScore.calculate(
                keigoResult.score(), appropriateness, uchiSoto, indirectness, professional);

        String feedback = generateFeedback(scenario, score, keigoResult);

        return new EvaluationResult(score, keigoResult.violations(), feedback);
    }

    private int evaluateAppropriateness(CulturalScenario scenario, String response) {
        int score = 60;
        List<String> keyPhrases = scenario.getKeyPhrases();
        List<String> avoidPhrases = scenario.getAvoidPhrases();

        if (keyPhrases != null) {
            long found = keyPhrases.stream().filter(response::contains).count();
            score += (int) (found * 10);
        }
        if (avoidPhrases != null) {
            long found = avoidPhrases.stream().filter(response::contains).count();
            score -= (int) (found * 15);
        }

        return Math.max(0, Math.min(100, score));
    }

    private int evaluateUchiSoto(CulturalScenario scenario, String response) {
        int score = 70;
        RelationshipType rel = scenario.getRelationship();

        if (rel == RelationshipType.TO_CLIENT || rel == RelationshipType.TO_EXTERNAL) {
            if (response.contains("弊社")) score += 15;
            if (response.contains("御社") || response.contains("貴社")) score += 15;
            if (response.contains("うちの会社")) score -= 20;
            if (response.contains("あなたの会社")) score -= 20;
        }

        return Math.max(0, Math.min(100, score));
    }

    private String generateFeedback(CulturalScenario scenario, CulturalScore score, KeigoValidationResult keigoResult) {
        StringBuilder fb = new StringBuilder();

        if (score.overallScore() >= 80) {
            fb.append("Excellent cultural awareness! ");
        } else if (score.overallScore() >= 60) {
            fb.append("Good attempt with room for improvement. ");
        } else {
            fb.append("This response needs significant cultural adjustment. ");
        }

        if (!keigoResult.violations().isEmpty()) {
            fb.append("Keigo issues detected: ").append(keigoResult.violations().size()).append(" violation(s). ");
        }

        if (scenario.getModelAnswer() != null) {
            fb.append("Model answer: ").append(scenario.getModelAnswer());
        }

        return fb.toString();
    }
}
