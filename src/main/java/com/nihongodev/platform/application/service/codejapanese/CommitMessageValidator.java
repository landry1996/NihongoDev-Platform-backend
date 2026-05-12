package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.application.service.codejapanese.steps.PrefixStep;
import com.nihongodev.platform.application.service.codejapanese.steps.ScopeStep;
import com.nihongodev.platform.application.service.codejapanese.steps.TaigenDomeStep;
import com.nihongodev.platform.application.service.codejapanese.steps.VerbEndingDetectionStep;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommitMessageValidator {

    private final List<CommitAnalysisStep> steps;
    private final PrefixStep prefixStep;
    private final ScopeStep scopeStep;
    private final TaigenDomeStep taigenDomeStep;
    private final VerbEndingDetectionStep verbEndingStep;

    public CommitMessageValidator(List<CommitAnalysisStep> steps, PrefixStep prefixStep,
                                  ScopeStep scopeStep, TaigenDomeStep taigenDomeStep,
                                  VerbEndingDetectionStep verbEndingStep) {
        this.steps = steps;
        this.prefixStep = prefixStep;
        this.scopeStep = scopeStep;
        this.taigenDomeStep = taigenDomeStep;
        this.verbEndingStep = verbEndingStep;
    }

    public CommitMessageAnalysis validate(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = steps.stream()
            .flatMap(step -> step.analyze(commitMessage, rule).stream())
            .toList();

        CommitPrefix detected = prefixStep.detectPrefix(commitMessage);
        boolean hasValidPrefix = detected != null;
        boolean isTaigenDome = taigenDomeStep.isTaigenDome(commitMessage);
        String scope = scopeStep.extractScope(commitMessage);
        boolean hasScope = scope != null;
        int length = commitMessage.length();
        List<String> detectedVerbEndings = verbEndingStep.detectVerbEndings(commitMessage);

        int score = calculateCommitScore(violations, hasValidPrefix, isTaigenDome, hasScope, length, rule);

        return new CommitMessageAnalysis(
            hasValidPrefix, detected, isTaigenDome, hasScope,
            scope, length, length <= rule.maxLength(),
            detectedVerbEndings, score
        );
    }

    private int calculateCommitScore(List<TechnicalJapaneseViolation> violations, boolean hasValidPrefix,
                                     boolean isTaigenDome, boolean hasScope, int length, CommitMessageRule rule) {
        int score = 100;

        if (!hasValidPrefix) score -= 30;
        if (rule.requireTaigenDome() && !isTaigenDome) score -= 25;
        if (rule.requireScope() && !hasScope) score -= 15;
        if (length > rule.maxLength()) score -= 10;

        for (TechnicalJapaneseViolation v : violations) {
            switch (v.severity()) {
                case CRITICAL -> score -= 20;
                case MODERATE -> score -= 10;
                case MINOR -> score -= 5;
                default -> {}
            }
        }

        return Math.max(0, Math.min(100, score));
    }
}
