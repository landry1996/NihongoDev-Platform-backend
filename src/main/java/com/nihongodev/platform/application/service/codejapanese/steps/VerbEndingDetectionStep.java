package com.nihongodev.platform.application.service.codejapanese.steps;

import com.nihongodev.platform.application.service.codejapanese.CommitAnalysisStep;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VerbEndingDetectionStep implements CommitAnalysisStep {

    private static final List<String> FORBIDDEN_VERB_ENDINGS = List.of(
        "しました", "します", "した", "する",
        "ました", "ます", "できた", "なった",
        "変えた", "追加した", "修正した", "削除した"
    );

    @Override
    public List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();

        List<String> detected = detectVerbEndings(commitMessage);
        for (String verbEnding : detected) {
            violations.add(new TechnicalJapaneseViolation(
                verbEnding,
                "体言止めを使用してください。動詞の活用形は避けてください。",
                ViolationType.VERB_ENDING_IN_COMMIT,
                "Forbidden verb ending detected: " + verbEnding,
                Severity.MODERATE
            ));
        }

        return violations;
    }

    public List<String> detectVerbEndings(String commitMessage) {
        List<String> detected = new ArrayList<>();
        for (String pattern : FORBIDDEN_VERB_ENDINGS) {
            if (commitMessage.contains(pattern)) {
                detected.add(pattern);
            }
        }
        return detected;
    }
}
