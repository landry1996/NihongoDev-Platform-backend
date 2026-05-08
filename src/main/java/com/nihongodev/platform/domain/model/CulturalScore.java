package com.nihongodev.platform.domain.model;

public record CulturalScore(
        int keigoScore,
        int appropriatenessScore,
        int uchiSotoScore,
        int indirectnessScore,
        int professionalToneScore,
        int overallScore
) {
    public static CulturalScore calculate(int keigo, int appropriateness, int uchiSoto, int indirectness, int professional) {
        int overall = (int) (keigo * 0.30 + appropriateness * 0.25 + uchiSoto * 0.20 + indirectness * 0.15 + professional * 0.10);
        return new CulturalScore(keigo, appropriateness, uchiSoto, indirectness, professional, overall);
    }

    public static CulturalScore zero() {
        return new CulturalScore(0, 0, 0, 0, 0, 0);
    }

    public static CulturalScore perfect() {
        return new CulturalScore(100, 100, 100, 100, 100, 100);
    }
}
