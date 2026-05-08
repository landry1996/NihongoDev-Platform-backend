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
public class UchiSotoStep implements KeigoAnalysisStep {

    @Override
    public List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        List<KeigoViolation> violations = new ArrayList<>();

        if (expectedLevel == KeigoLevel.CASUAL) {
            return violations;
        }

        boolean speakingToExternal = relationship == RelationshipType.TO_CLIENT || relationship == RelationshipType.TO_EXTERNAL;

        if (speakingToExternal) {
            if (text.contains("うちの会社") || text.contains("私たちの会社")) {
                violations.add(new KeigoViolation(
                        text.contains("うちの会社") ? "うちの会社" : "私たちの会社",
                        "弊社",
                        KeigoLevel.CASUAL, expectedLevel,
                        "uchi_soto_company_self", Severity.CRITICAL
                ));
            }
            if (text.contains("あなたの会社") || text.contains("そちらの会社")) {
                violations.add(new KeigoViolation(
                        text.contains("あなたの会社") ? "あなたの会社" : "そちらの会社",
                        "御社/貴社",
                        KeigoLevel.CASUAL, expectedLevel,
                        "uchi_soto_company_other", Severity.CRITICAL
                ));
            }
            if (text.contains("社長") && !text.contains("弊社") && text.contains("うちの")) {
                violations.add(new KeigoViolation(
                        "うちの社長", "弊社の[名前]",
                        KeigoLevel.CASUAL, expectedLevel,
                        "uchi_soto_superior_to_external", Severity.MODERATE
                ));
            }
        }

        return violations;
    }
}
