package com.nihongodev.platform.domain.model;

public record CodeExerciseScore(
    int technicalAccuracyScore,
    int japaneseQualityScore,
    int professionalToneScore,
    int structureScore,
    int teamCommunicationScore,
    int overallScore
) {
    public static CodeExerciseScore calculate(int technical, int japanese, int professional, int structure, int teamComm) {
        int overall = (int) (technical * 0.30 + japanese * 0.25 + professional * 0.20 + structure * 0.15 + teamComm * 0.10);
        return new CodeExerciseScore(technical, japanese, professional, structure, teamComm, overall);
    }
}
