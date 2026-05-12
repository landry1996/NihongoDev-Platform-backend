package com.nihongodev.platform.domain.model;

public enum ActivityType {
    LESSON_COMPLETED(50, 0.5),
    QUIZ_COMPLETED(30, 1.0),
    INTERVIEW_COMPLETED(80, 1.5),
    CORRECTION_COMPLETED(40, 1.2),
    CULTURAL_SCENARIO_COMPLETED(60, 1.3),
    CODE_EXERCISE_COMPLETED(70, 1.4),
    REAL_CONTENT_COMPLETED(45, 1.1);

    private final int baseXp;
    private final double scoreWeight;

    ActivityType(int baseXp, double scoreWeight) {
        this.baseXp = baseXp;
        this.scoreWeight = scoreWeight;
    }

    public int getBaseXp() { return baseXp; }
    public double getScoreWeight() { return scoreWeight; }

    public int calculateXp(double score) {
        if (this == LESSON_COMPLETED) return baseXp;
        return (int) (baseXp + (score * scoreWeight));
    }

    public static ActivityType fromModuleType(ModuleType moduleType) {
        return switch (moduleType) {
            case LESSON -> LESSON_COMPLETED;
            case QUIZ -> QUIZ_COMPLETED;
            case INTERVIEW -> INTERVIEW_COMPLETED;
            case CORRECTION -> CORRECTION_COMPLETED;
            case CULTURAL -> CULTURAL_SCENARIO_COMPLETED;
            case CODE_REVIEW -> CODE_EXERCISE_COMPLETED;
            case REAL_CONTENT -> REAL_CONTENT_COMPLETED;
            default -> throw new IllegalArgumentException("No activity type for module: " + moduleType);
        };
    }
}
