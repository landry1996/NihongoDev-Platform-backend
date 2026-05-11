package com.nihongodev.platform.application.dto;

public record CulturalScoreDto(
        int keigoScore,
        int appropriatenessScore,
        int uchiSotoScore,
        int indirectnessScore,
        int professionalToneScore,
        int overallScore
) {}
