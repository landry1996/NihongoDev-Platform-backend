package com.nihongodev.platform.application.service.codejapanese.steps;

import com.nihongodev.platform.application.service.codejapanese.CommitAnalysisStep;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrefixStep implements CommitAnalysisStep {

    @Override
    public List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();

        CommitPrefix detected = detectPrefix(commitMessage);
        if (detected == null) {
            violations.add(new TechnicalJapaneseViolation(
                commitMessage.substring(0, Math.min(20, commitMessage.length())),
                rule.expectedPrefix().getEnglish() + ": で始めてください",
                ViolationType.WRONG_STRUCTURE,
                "Commit message must start with a valid prefix",
                Severity.CRITICAL
            ));
        } else if (rule.expectedPrefix() != null && detected != rule.expectedPrefix()) {
            violations.add(new TechnicalJapaneseViolation(
                detected.getEnglish(),
                rule.expectedPrefix().getEnglish() + " (" + rule.expectedPrefix().getJapanese() + ")",
                ViolationType.WRONG_STRUCTURE,
                "Expected prefix: " + rule.expectedPrefix().getEnglish(),
                Severity.MODERATE
            ));
        }

        return violations;
    }

    public CommitPrefix detectPrefix(String commitMessage) {
        String lower = commitMessage.toLowerCase();
        for (CommitPrefix prefix : CommitPrefix.values()) {
            if (lower.startsWith(prefix.getEnglish() + "(") || lower.startsWith(prefix.getEnglish() + ":")) {
                return prefix;
            }
        }
        return null;
    }
}
