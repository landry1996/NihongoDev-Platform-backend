package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.DifficultyLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScoreCalculator")
class ScoreCalculatorTest {

    @Test
    @DisplayName("should calculate base points with no streak and no time bonus")
    void shouldCalculateBasePoints() {
        double points = ScoreCalculator.calculatePoints(2, 30, 1.0, DifficultyLevel.MEDIUM);
        assertThat(points).isGreaterThan(0);
    }

    @Test
    @DisplayName("should apply streak multiplier")
    void shouldApplyStreakMultiplier() {
        double pointsNoStreak = ScoreCalculator.calculatePoints(2, 10, 1.0, DifficultyLevel.MEDIUM);
        double pointsWithStreak = ScoreCalculator.calculatePoints(2, 10, 2.0, DifficultyLevel.MEDIUM);
        assertThat(pointsWithStreak).isGreaterThan(pointsNoStreak);
    }

    @Test
    @DisplayName("should give higher score for harder difficulty")
    void shouldGiveHigherScoreForHarderDifficulty() {
        double easyPoints = ScoreCalculator.calculatePoints(2, 10, 1.0, DifficultyLevel.EASY);
        double hardPoints = ScoreCalculator.calculatePoints(2, 10, 1.0, DifficultyLevel.HARD);
        assertThat(hardPoints).isGreaterThan(easyPoints);
    }

    @Test
    @DisplayName("should give time bonus for fast answers")
    void shouldGiveTimeBonusForFastAnswers() {
        double fastAnswer = ScoreCalculator.calculatePoints(2, 3, 1.0, DifficultyLevel.MEDIUM);
        double slowAnswer = ScoreCalculator.calculatePoints(2, 25, 1.0, DifficultyLevel.MEDIUM);
        assertThat(fastAnswer).isGreaterThanOrEqualTo(slowAnswer);
    }

    @Test
    @DisplayName("should handle zero base points gracefully")
    void shouldHandleZeroBasePoints() {
        double points = ScoreCalculator.calculatePoints(0, 10, 1.0, DifficultyLevel.MEDIUM);
        assertThat(points).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("should handle max streak multiplier")
    void shouldHandleMaxStreakMultiplier() {
        double points = ScoreCalculator.calculatePoints(2, 5, 3.0, DifficultyLevel.HARD);
        assertThat(points).isGreaterThan(0);
    }
}
