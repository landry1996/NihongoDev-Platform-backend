package com.nihongodev.platform.domain.model;

import java.util.List;

public record CommitMessageAnalysis(
    boolean hasValidPrefix,
    CommitPrefix detectedPrefix,
    boolean isTaigenDome,
    boolean hasScope,
    String detectedScope,
    int length,
    boolean isWithinMaxLength,
    List<String> detectedVerbEndings,
    int commitScore
) {}
