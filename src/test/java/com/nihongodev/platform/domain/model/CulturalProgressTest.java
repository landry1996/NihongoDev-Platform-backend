package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CulturalProgress Domain Model")
class CulturalProgressTest {

    @Test
    @DisplayName("should initialize with correct defaults")
    void shouldInitializeWithDefaults() {
        UUID userId = UUID.randomUUID();
        CulturalProgress progress = CulturalProgress.initialize(userId, ScenarioCategory.COMMUNICATION);

        assertThat(progress.getId()).isNotNull();
        assertThat(progress.getUserId()).isEqualTo(userId);
        assertThat(progress.getCategory()).isEqualTo(ScenarioCategory.COMMUNICATION);
        assertThat(progress.getScenariosCompleted()).isEqualTo(0);
        assertThat(progress.getTotalScore()).isEqualTo(0);
        assertThat(progress.getAverageScore()).isEqualTo(0);
        assertThat(progress.getBestScore()).isEqualTo(0);
        assertThat(progress.getCurrentStreak()).isEqualTo(0);
        assertThat(progress.getUnlockedLevel()).isEqualTo(KeigoLevel.TEINEIGO);
    }

    @Test
    @DisplayName("should increment streak when score >= 70")
    void shouldIncrementStreakAboveThreshold() {
        CulturalProgress progress = CulturalProgress.initialize(UUID.randomUUID(), ScenarioCategory.SOCIAL);

        progress.recordAttempt(75);
        assertThat(progress.getCurrentStreak()).isEqualTo(1);

        progress.recordAttempt(80);
        assertThat(progress.getCurrentStreak()).isEqualTo(2);

        progress.recordAttempt(70);
        assertThat(progress.getCurrentStreak()).isEqualTo(3);
        assertThat(progress.getScenariosCompleted()).isEqualTo(3);
    }

    @Test
    @DisplayName("should reset streak when score < 70")
    void shouldResetStreakBelowThreshold() {
        CulturalProgress progress = CulturalProgress.initialize(UUID.randomUUID(), ScenarioCategory.REPORTING);

        progress.recordAttempt(80);
        progress.recordAttempt(90);
        assertThat(progress.getCurrentStreak()).isEqualTo(2);

        progress.recordAttempt(69);
        assertThat(progress.getCurrentStreak()).isEqualTo(0);
        assertThat(progress.getScenariosCompleted()).isEqualTo(3);
        assertThat(progress.getBestScore()).isEqualTo(90);
    }

    @Test
    @DisplayName("should update average score correctly across attempts")
    void shouldUpdateAverageScore() {
        CulturalProgress progress = CulturalProgress.initialize(UUID.randomUUID(), ScenarioCategory.COMMUNICATION);

        progress.recordAttempt(60);
        assertThat(progress.getAverageScore()).isEqualTo(60);

        progress.recordAttempt(80);
        assertThat(progress.getAverageScore()).isEqualTo(70);

        progress.recordAttempt(90);
        assertThat(progress.getAverageScore()).isEqualTo(76);
    }
}
