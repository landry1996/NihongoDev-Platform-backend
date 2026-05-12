package com.nihongodev.platform.application.service.codejapanese.steps;

import com.nihongodev.platform.application.service.codejapanese.CommitAnalysisStep;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LengthStep implements CommitAnalysisStep {

    @Override
    public List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();

        if (commitMessage.length() > rule.maxLength()) {
            violations.add(new TechnicalJapaneseViolation(
                "Length: " + commitMessage.length(),
                "最大" + rule.maxLength() + "文字以内にしてください",
                ViolationType.WRONG_STRUCTURE,
                "Commit message exceeds max length of " + rule.maxLength(),
                Severity.MINOR
            ));
        }

        return violations;
    }
}
