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
public class BusinessExpressionStep implements KeigoAnalysisStep {

    private static final Map<String, String> BUSINESS_UPGRADES = Map.of(
            "すみません", "申し訳ございません",
            "ごめんなさい", "申し訳ございません",
            "わかりました", "承知いたしました",
            "いいですか", "よろしいでしょうか",
            "できません", "いたしかねます",
            "知りません", "存じません",
            "どうですか", "いかがでしょうか"
    );

    @Override
    public List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        List<KeigoViolation> violations = new ArrayList<>();

        if (expectedLevel == KeigoLevel.CASUAL || expectedLevel == KeigoLevel.TEINEIGO) {
            return violations;
        }

        if (relationship == RelationshipType.TO_CLIENT || relationship == RelationshipType.TO_SUPERIOR || relationship == RelationshipType.TO_EXTERNAL) {
            for (Map.Entry<String, String> entry : BUSINESS_UPGRADES.entrySet()) {
                if (text.contains(entry.getKey())) {
                    violations.add(new KeigoViolation(
                            entry.getKey(), entry.getValue(),
                            KeigoLevel.TEINEIGO, expectedLevel,
                            "business_expression_upgrade", Severity.MINOR
                    ));
                }
            }
        }

        return violations;
    }
}
