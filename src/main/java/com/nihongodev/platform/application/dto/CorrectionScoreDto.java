package com.nihongodev.platform.application.dto;

public record CorrectionScoreDto(
        double grammarScore,
        double vocabularyScore,
        double politenessScore,
        double clarityScore,
        double naturalnessScore,
        double professionalScore,
        double overallScore
) {}
