package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.DifficultyLevel;

public final class ScoreCalculator {

    private ScoreCalculator() {}

    public static double calculatePoints(int basePoints, int timeSpentSeconds, double streakMultiplier, DifficultyLevel difficulty) {
        double timeBonus = calculateTimeBonus(timeSpentSeconds);
        double difficultyMultiplier = getDifficultyMultiplier(difficulty);
        return basePoints * timeBonus * streakMultiplier * difficultyMultiplier;
    }

    public static double calculateTimeBonus(int timeSpentSeconds) {
        if (timeSpentSeconds <= 5) return 1.5;
        if (timeSpentSeconds <= 15) return 1.0;
        if (timeSpentSeconds <= 30) return 0.75;
        return 0.5;
    }

    public static double getDifficultyMultiplier(DifficultyLevel difficulty) {
        if (difficulty == null) return 1.0;
        return switch (difficulty) {
            case EASY -> 0.8;
            case MEDIUM -> 1.0;
            case HARD -> 1.5;
        };
    }
}
