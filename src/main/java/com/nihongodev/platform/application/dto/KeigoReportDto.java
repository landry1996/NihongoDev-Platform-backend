package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.KeigoViolation;

import java.util.List;
import java.util.Map;

public record KeigoReportDto(
        int totalAttempts,
        int averageKeigoScore,
        KeigoLevel currentLevel,
        List<KeigoViolation> frequentViolations,
        Map<String, Integer> violationsByRule
) {}
