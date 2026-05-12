package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ViolationType;

import java.util.Map;

public record ViolationReportDto(
    Map<ViolationType, Integer> violationCounts,
    int totalViolations,
    ViolationType mostCommonViolation
) {}
