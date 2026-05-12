package com.nihongodev.platform.domain.model;

import java.util.List;

public record CommitMessageRule(
    CommitPrefix expectedPrefix,
    boolean requireTaigenDome,
    int maxLength,
    boolean requireScope,
    String expectedScope,
    List<String> forbiddenPatterns,
    List<String> goodExamples,
    List<String> badExamples
) {}
