package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.CommitPrefix;

import java.util.List;

public record CommitMessageAnalysisDto(
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
