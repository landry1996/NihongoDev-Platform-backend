package com.nihongodev.platform.application.service.cultural;

import com.nihongodev.platform.domain.model.CulturalScenario;
import com.nihongodev.platform.domain.model.CulturalScore;
import com.nihongodev.platform.domain.model.KeigoViolation;

import java.util.List;
import java.util.UUID;

public interface ScenarioEvaluator {
    EvaluationResult evaluate(CulturalScenario scenario, String userResponse, UUID selectedChoiceId);

    record EvaluationResult(CulturalScore score, List<KeigoViolation> violations, String feedback) {}
}
