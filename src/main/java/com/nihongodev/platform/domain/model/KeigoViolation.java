package com.nihongodev.platform.domain.model;

public record KeigoViolation(
        String originalText,
        String suggestion,
        KeigoLevel usedLevel,
        KeigoLevel expectedLevel,
        String rule,
        Severity severity
) {}
