package com.nihongodev.platform.application.service.cultural.steps;

import com.nihongodev.platform.application.service.cultural.KeigoAnalysisStep;
import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.KeigoViolation;
import com.nihongodev.platform.domain.model.RelationshipType;
import com.nihongodev.platform.domain.model.Severity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PronounStep implements KeigoAnalysisStep {

    @Override
    public List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        List<KeigoViolation> violations = new ArrayList<>();

        if (expectedLevel != KeigoLevel.CASUAL) {
            if (text.contains("俺")) {
                violations.add(new KeigoViolation(
                        "俺", "私", KeigoLevel.CASUAL, expectedLevel,
                        "casual_pronoun_ore", Severity.CRITICAL
                ));
            }
            if (text.contains("僕") && (expectedLevel == KeigoLevel.SONKEIGO || expectedLevel == KeigoLevel.KENJOUGO)) {
                violations.add(new KeigoViolation(
                        "僕", "私", KeigoLevel.TEINEIGO, expectedLevel,
                        "informal_pronoun_boku", Severity.MINOR
                ));
            }
            if (text.contains("あなた")) {
                violations.add(new KeigoViolation(
                        "あなた", "[名前]様/さん", KeigoLevel.CASUAL, expectedLevel,
                        "avoid_anata", Severity.MODERATE
                ));
            }
        }

        return violations;
    }
}
