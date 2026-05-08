package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.application.service.cultural.ScenarioEvaluator;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MultipleChoiceEvaluator implements ScenarioEvaluator {

    @Override
    public EvaluationResult evaluate(CulturalScenario scenario, String userResponse, UUID selectedChoiceId) {
        List<ScenarioChoice> choices = scenario.getChoices();
        if (choices == null || choices.isEmpty()) {
            return new EvaluationResult(CulturalScore.zero(), List.of(), "No choices available for this scenario.");
        }

        int selectedIndex = resolveChoiceIndex(selectedChoiceId, choices.size());
        ScenarioChoice selected = choices.get(selectedIndex);
        ScenarioChoice optimal = choices.stream()
                .filter(ScenarioChoice::isOptimal)
                .findFirst()
                .orElse(choices.get(0));

        int baseScore = selected.culturalScore();
        CulturalScore score = CulturalScore.calculate(baseScore, baseScore, baseScore, baseScore, baseScore);

        String feedback = selected.feedbackIfChosen();
        if (!selected.isOptimal() && optimal != null) {
            feedback += " The best response was: " + optimal.textJp();
        }

        return new EvaluationResult(score, List.of(), feedback);
    }

    private int resolveChoiceIndex(UUID selectedChoiceId, int size) {
        if (selectedChoiceId == null) {
            return 0;
        }
        int index = Math.abs(selectedChoiceId.hashCode()) % size;
        return Math.min(index, size - 1);
    }
}
