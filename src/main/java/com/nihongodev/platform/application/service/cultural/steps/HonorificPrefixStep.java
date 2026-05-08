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
public class HonorificPrefixStep implements KeigoAnalysisStep {

    private static final Map<String, String> HONORIFIC_WORDS = Map.of(
            "名前", "お名前",
            "時間", "お時間",
            "連絡", "ご連絡",
            "確認", "ご確認",
            "報告", "ご報告",
            "返事", "お返事",
            "忙しい", "お忙しい"
    );

    @Override
    public List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        List<KeigoViolation> violations = new ArrayList<>();

        if (expectedLevel == KeigoLevel.CASUAL || expectedLevel == KeigoLevel.TEINEIGO) {
            return violations;
        }

        if (relationship == RelationshipType.TO_CLIENT || relationship == RelationshipType.TO_SUPERIOR || relationship == RelationshipType.TO_EXTERNAL) {
            for (Map.Entry<String, String> entry : HONORIFIC_WORDS.entrySet()) {
                if (text.contains(entry.getKey()) && !text.contains(entry.getValue())) {
                    violations.add(new KeigoViolation(
                            entry.getKey(), entry.getValue(),
                            KeigoLevel.TEINEIGO, expectedLevel,
                            "honorific_prefix_missing", Severity.MINOR
                    ));
                }
            }
        }

        return violations;
    }
}
