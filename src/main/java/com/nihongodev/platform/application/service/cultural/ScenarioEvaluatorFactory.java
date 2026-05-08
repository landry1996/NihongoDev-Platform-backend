package com.nihongodev.platform.application.service.cultural;

import com.nihongodev.platform.application.service.cultural.evaluators.FreeTextEvaluator;
import com.nihongodev.platform.application.service.cultural.evaluators.MultipleChoiceEvaluator;
import com.nihongodev.platform.application.service.cultural.evaluators.RolePlayEvaluator;
import com.nihongodev.platform.domain.model.ScenarioMode;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class ScenarioEvaluatorFactory {

    private final Map<ScenarioMode, ScenarioEvaluator> evaluators;

    public ScenarioEvaluatorFactory(MultipleChoiceEvaluator multipleChoice,
                                    FreeTextEvaluator freeText,
                                    RolePlayEvaluator rolePlay) {
        this.evaluators = new EnumMap<>(ScenarioMode.class);
        this.evaluators.put(ScenarioMode.MULTIPLE_CHOICE, multipleChoice);
        this.evaluators.put(ScenarioMode.FREE_TEXT, freeText);
        this.evaluators.put(ScenarioMode.ROLE_PLAY, rolePlay);
    }

    public ScenarioEvaluator getEvaluator(ScenarioMode mode) {
        ScenarioEvaluator evaluator = evaluators.get(mode);
        if (evaluator == null) {
            throw new IllegalArgumentException("No evaluator for mode: " + mode);
        }
        return evaluator;
    }
}
