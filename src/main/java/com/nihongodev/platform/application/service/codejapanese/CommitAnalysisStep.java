package com.nihongodev.platform.application.service.codejapanese;

import com.nihongodev.platform.domain.model.CommitMessageRule;
import com.nihongodev.platform.domain.model.TechnicalJapaneseViolation;

import java.util.List;

public interface CommitAnalysisStep {
    List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule);
}
