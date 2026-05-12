package com.nihongodev.platform.application.dto;

public record CodeExerciseScoreDto(
    int technicalAccuracyScore,
    int japaneseQualityScore,
    int professionalToneScore,
    int structureScore,
    int teamCommunicationScore,
    int overallScore
) {}
