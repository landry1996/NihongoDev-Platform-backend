package com.nihongodev.platform.application.service.codejapanese.steps;

import com.nihongodev.platform.application.service.codejapanese.CommitAnalysisStep;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ScopeStep implements CommitAnalysisStep {

    private static final Pattern SCOPE_PATTERN = Pattern.compile("^\\w+\\(([^)]+)\\):");

    @Override
    public List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = new ArrayList<>();

        if (!rule.requireScope()) {
            return violations;
        }

        String scope = extractScope(commitMessage);
        if (scope == null) {
            violations.add(new TechnicalJapaneseViolation(
                commitMessage.substring(0, Math.min(30, commitMessage.length())),
                "prefix(scope): の形式を使用してください",
                ViolationType.WRONG_STRUCTURE,
                "Scope is required",
                Severity.MODERATE
            ));
        } else if (rule.expectedScope() != null && !scope.equals(rule.expectedScope())) {
            violations.add(new TechnicalJapaneseViolation(
                scope,
                rule.expectedScope(),
                ViolationType.WRONG_STRUCTURE,
                "Expected scope: " + rule.expectedScope(),
                Severity.MINOR
            ));
        }

        return violations;
    }

    public String extractScope(String commitMessage) {
        Matcher matcher = SCOPE_PATTERN.matcher(commitMessage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
