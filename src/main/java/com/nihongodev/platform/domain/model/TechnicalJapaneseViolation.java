package com.nihongodev.platform.domain.model;

public record TechnicalJapaneseViolation(
    String originalText,
    String suggestion,
    ViolationType violationType,
    String rule,
    Severity severity
) {}
