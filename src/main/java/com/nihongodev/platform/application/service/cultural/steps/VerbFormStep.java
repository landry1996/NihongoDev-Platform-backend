package com.nihongodev.platform.application.service.cultural.steps;

import com.nihongodev.platform.application.service.cultural.KeigoAnalysisStep;
import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.KeigoViolation;
import com.nihongodev.platform.domain.model.RelationshipType;
import com.nihongodev.platform.domain.model.Severity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class VerbFormStep implements KeigoAnalysisStep {

    private static final Map<String, String> SONKEIGO_VERBS = Map.of(
            "する", "なさる",
            "行く", "いらっしゃる",
            "来る", "いらっしゃる",
            "食べる", "召し上がる",
            "見る", "ご覧になる",
            "言う", "おっしゃる",
            "いる", "いらっしゃる",
            "くれる", "くださる"
    );

    private static final Map<String, String> KENJOUGO_VERBS = Map.of(
            "する", "いたす",
            "行く", "参る",
            "来る", "参る",
            "食べる", "いただく",
            "見る", "拝見する",
            "言う", "申す",
            "いる", "おる",
            "もらう", "いただく"
    );

    @Override
    public List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        List<KeigoViolation> violations = new ArrayList<>();

        if (expectedLevel == KeigoLevel.SONKEIGO || expectedLevel == KeigoLevel.MIXED_FORMAL) {
            if (relationship == RelationshipType.TO_CLIENT || relationship == RelationshipType.TO_SUPERIOR) {
                for (Map.Entry<String, String> entry : SONKEIGO_VERBS.entrySet()) {
                    if (text.contains(entry.getKey()) && !text.contains(entry.getValue())) {
                        violations.add(new KeigoViolation(
                                entry.getKey(), entry.getValue(),
                                KeigoLevel.CASUAL, expectedLevel,
                                "sonkeigo_verb_required", Severity.MODERATE
                        ));
                    }
                }
            }
        }

        if (expectedLevel == KeigoLevel.KENJOUGO || expectedLevel == KeigoLevel.MIXED_FORMAL) {
            for (Map.Entry<String, String> entry : KENJOUGO_VERBS.entrySet()) {
                if (text.contains(entry.getKey()) && !text.contains(entry.getValue())) {
                    boolean refersSelf = text.contains("私") || text.contains("弊社") || text.contains("当社");
                    if (refersSelf) {
                        violations.add(new KeigoViolation(
                                entry.getKey(), entry.getValue(),
                                KeigoLevel.CASUAL, expectedLevel,
                                "kenjougo_verb_required", Severity.MODERATE
                        ));
                    }
                }
            }
        }

        return violations;
    }
}
