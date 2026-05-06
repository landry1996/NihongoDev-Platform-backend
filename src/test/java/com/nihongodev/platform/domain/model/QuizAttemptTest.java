package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QuizAttempt Domain Model")
class QuizAttemptTest {

    @Test
    @DisplayName("should start with correct initial state")
    void shouldStartWithCorrectState() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);

        assertThat(attempt.getId()).isNotNull();
        assertThat(attempt.getStatus()).isEqualTo(AttemptStatus.IN_PROGRESS);
        assertThat(attempt.getCurrentStreak()).isEqualTo(0);
        assertThat(attempt.getMaxStreak()).isEqualTo(0);
        assertThat(attempt.getLivesRemaining()).isEqualTo(-1);
        assertThat(attempt.isInProgress()).isTrue();
    }

    @Test
    @DisplayName("should initialize survival mode with 3 lives")
    void shouldInitializeSurvivalWith3Lives() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.SURVIVAL);

        assertThat(attempt.getLivesRemaining()).isEqualTo(3);
        assertThat(attempt.getMode()).isEqualTo(QuizMode.SURVIVAL);
    }

    @Test
    @DisplayName("should track streak correctly")
    void shouldTrackStreak() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);

        attempt.recordCorrectAnswer();
        attempt.recordCorrectAnswer();
        attempt.recordCorrectAnswer();

        assertThat(attempt.getCurrentStreak()).isEqualTo(3);
        assertThat(attempt.getMaxStreak()).isEqualTo(3);
    }

    @Test
    @DisplayName("should reset current streak on incorrect answer")
    void shouldResetStreakOnIncorrect() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);

        attempt.recordCorrectAnswer();
        attempt.recordCorrectAnswer();
        attempt.recordCorrectAnswer();
        attempt.recordIncorrectAnswer();

        assertThat(attempt.getCurrentStreak()).isEqualTo(0);
        assertThat(attempt.getMaxStreak()).isEqualTo(3);
    }

    @Test
    @DisplayName("should lose life in survival mode on incorrect answer")
    void shouldLoseLifeInSurvival() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.SURVIVAL);

        attempt.recordIncorrectAnswer();

        assertThat(attempt.getLivesRemaining()).isEqualTo(2);
        assertThat(attempt.isInProgress()).isTrue();
    }

    @Test
    @DisplayName("should game over when all lives lost")
    void shouldGameOverWhenAllLivesLost() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.SURVIVAL);

        attempt.recordIncorrectAnswer();
        attempt.recordIncorrectAnswer();
        attempt.recordIncorrectAnswer();

        assertThat(attempt.getLivesRemaining()).isEqualTo(0);
        assertThat(attempt.isGameOver()).isTrue();
        assertThat(attempt.isInProgress()).isFalse();
    }

    @Test
    @DisplayName("should calculate streak multiplier correctly")
    void shouldCalculateStreakMultiplier() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);

        assertThat(attempt.getStreakMultiplier()).isEqualTo(1.0);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(1.0);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(1.5);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(1.5);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(2.0);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(2.0);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(2.5);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(2.5);

        attempt.recordCorrectAnswer();
        assertThat(attempt.getStreakMultiplier()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("should complete attempt correctly")
    void shouldCompleteAttempt() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.TIMED);

        attempt.complete(180);

        assertThat(attempt.getStatus()).isEqualTo(AttemptStatus.COMPLETED);
        assertThat(attempt.getTimeSpentSeconds()).isEqualTo(180);
        assertThat(attempt.getCompletedAt()).isNotNull();
        assertThat(attempt.isInProgress()).isFalse();
    }

    @Test
    @DisplayName("should not lose lives in classic mode")
    void shouldNotLoseLivesInClassic() {
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);

        attempt.recordIncorrectAnswer();
        attempt.recordIncorrectAnswer();
        attempt.recordIncorrectAnswer();

        assertThat(attempt.getLivesRemaining()).isEqualTo(-1);
        assertThat(attempt.isInProgress()).isTrue();
        assertThat(attempt.isGameOver()).isFalse();
    }
}
