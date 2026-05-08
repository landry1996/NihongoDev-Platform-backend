package com.nihongodev.platform.application.service.cultural.evaluators;

import com.nihongodev.platform.application.service.cultural.KeigoValidationResult;
import com.nihongodev.platform.application.service.cultural.KeigoValidator;
import com.nihongodev.platform.application.service.cultural.ScenarioEvaluator;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RolePlayEvaluator implements ScenarioEvaluator {

    private final KeigoValidator keigoValidator;
    private final IndirectnessAnalyzer indirectnessAnalyzer;

    public RolePlayEvaluator(KeigoValidator keigoValidator, IndirectnessAnalyzer indirectnessAnalyzer) {
        this.keigoValidator = keigoValidator;
        this.indirectnessAnalyzer = indirectnessAnalyzer;
    }

    @Override
    public EvaluationResult evaluate(CulturalScenario scenario, String userResponse, UUID selectedChoiceId) {
        if (userResponse == null || userResponse.isBlank()) {
            return new EvaluationResult(CulturalScore.zero(), List.of(), "No response provided.");
        }

        KeigoValidationResult keigoResult = keigoValidator.validate(
                userResponse, scenario.getExpectedKeigoLevel(), scenario.getRelationship());

        int indirectness = indirectnessAnalyzer.score(userResponse, scenario.getContext());

        int appropriateness = 60;
        if (scenario.getKeyPhrases() != null) {
            long found = scenario.getKeyPhrases().stream().filter(userResponse::contains).count();
            appropriateness += (int) (found * 10);
        }
        appropriateness = Math.min(100, appropriateness);

        CulturalScore score = CulturalScore.calculate(
                keigoResult.score(), appropriateness, 70, indirectness, 70);

        String feedback = "Role-play evaluated. Keigo score: " + keigoResult.score() + "/100. " +
                (keigoResult.violations().isEmpty() ? "No keigo violations." :
                        keigoResult.violations().size() + " keigo violation(s) detected.");

        return new EvaluationResult(score, keigoResult.violations(), feedback);
    }
}
