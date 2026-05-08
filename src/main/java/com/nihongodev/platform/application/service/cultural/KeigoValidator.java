package com.nihongodev.platform.application.service.cultural;

import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.KeigoViolation;
import com.nihongodev.platform.domain.model.RelationshipType;
import com.nihongodev.platform.domain.model.Severity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeigoValidator {

    private final List<KeigoAnalysisStep> steps;

    public KeigoValidator(List<KeigoAnalysisStep> steps) {
        this.steps = steps;
    }

    public KeigoValidationResult validate(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        if (text == null || text.isBlank()) {
            return KeigoValidationResult.perfect();
        }

        List<KeigoViolation> allViolations = steps.stream()
                .flatMap(step -> step.analyze(text, expectedLevel, relationship).stream())
                .toList();

        int score = calculateScore(allViolations);
        return new KeigoValidationResult(score, allViolations);
    }

    private int calculateScore(List<KeigoViolation> violations) {
        if (violations.isEmpty()) {
            return 100;
        }
        int penalty = violations.stream()
                .mapToInt(v -> switch (v.severity()) {
                    case CRITICAL -> 25;
                    case MODERATE -> 15;
                    case MINOR -> 8;
                    default -> 0;
                })
                .sum();
        return Math.max(0, 100 - penalty);
    }
}
