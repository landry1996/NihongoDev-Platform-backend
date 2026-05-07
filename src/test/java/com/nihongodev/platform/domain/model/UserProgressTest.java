package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserProgress")
class UserProgressTest {

    private UserProgress progress;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        progress = UserProgress.initialize(userId);
    }

    @Test
    @DisplayName("should initialize with BEGINNER level and zero counters")
    void shouldInitialize() {
        assertThat(progress.getUserId()).isEqualTo(userId);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.BEGINNER);
        assertThat(progress.getTotalXp()).isZero();
        assertThat(progress.getTotalLessonsCompleted()).isZero();
        assertThat(progress.getTotalQuizzesCompleted()).isZero();
        assertThat(progress.getTotalInterviewsCompleted()).isZero();
        assertThat(progress.getTotalCorrectionsCompleted()).isZero();
        assertThat(progress.getCurrentStreak()).isZero();
        assertThat(progress.getLongestStreak()).isZero();
        assertThat(progress.getGlobalScore()).isZero();
    }

    @Test
    @DisplayName("should increment lesson counter and add base XP")
    void shouldRecordLessonCompleted() {
        progress.recordActivity(ActivityType.LESSON_COMPLETED, 0);

        assertThat(progress.getTotalLessonsCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(50);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.BEGINNER);
    }

    @Test
    @DisplayName("should increment quiz counter and calculate XP from score")
    void shouldRecordQuizCompleted() {
        progress.recordActivity(ActivityType.QUIZ_COMPLETED, 85.0);

        assertThat(progress.getTotalQuizzesCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(115); // 30 + (85 * 1.0)
    }

    @Test
    @DisplayName("should increment interview counter and calculate XP")
    void shouldRecordInterviewCompleted() {
        progress.recordActivity(ActivityType.INTERVIEW_COMPLETED, 70.0);

        assertThat(progress.getTotalInterviewsCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(185); // 80 + (70 * 1.5)
    }

    @Test
    @DisplayName("should increment correction counter and calculate XP")
    void shouldRecordCorrectionCompleted() {
        progress.recordActivity(ActivityType.CORRECTION_COMPLETED, 80.0);

        assertThat(progress.getTotalCorrectionsCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(136); // 40 + (80 * 1.2)
    }

    @Test
    @DisplayName("should level up to INTERMEDIATE at 1000 XP")
    void shouldLevelUpToIntermediate() {
        for (int i = 0; i < 20; i++) {
            progress.recordActivity(ActivityType.LESSON_COMPLETED, 0); // 20 * 50 = 1000 XP
        }

        assertThat(progress.getTotalXp()).isEqualTo(1000);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.INTERMEDIATE);
    }

    @Test
    @DisplayName("should level up to ADVANCED at 5000 XP")
    void shouldLevelUpToAdvanced() {
        for (int i = 0; i < 100; i++) {
            progress.recordActivity(ActivityType.LESSON_COMPLETED, 0); // 100 * 50 = 5000 XP
        }

        assertThat(progress.getTotalXp()).isEqualTo(5000);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.ADVANCED);
    }

    @Test
    @DisplayName("should update global score with weighted average")
    void shouldUpdateGlobalScore() {
        progress.recordActivity(ActivityType.QUIZ_COMPLETED, 80.0);
        progress.updateGlobalScore(ActivityType.QUIZ_COMPLETED, 80.0);

        assertThat(progress.getGlobalScore()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should increment streak on first activity of the day")
    void shouldIncrementStreak() {
        progress.updateStreak(LocalDateTime.now());

        assertThat(progress.getCurrentStreak()).isEqualTo(1);
        assertThat(progress.getLongestStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("should not increment streak for second activity same day")
    void shouldNotIncrementStreakSameDay() {
        LocalDateTime now = LocalDateTime.now();
        progress.updateStreak(now);
        progress.updateStreak(now);

        assertThat(progress.getCurrentStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("should track longest streak")
    void shouldTrackLongestStreak() {
        LocalDateTime day1 = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 5, 2, 10, 0);
        LocalDateTime day3 = LocalDateTime.of(2026, 5, 3, 10, 0);

        progress.updateStreak(day1);
        progress.updateStreak(day2);
        progress.updateStreak(day3);

        assertThat(progress.getCurrentStreak()).isEqualTo(3);
        assertThat(progress.getLongestStreak()).isEqualTo(3);
    }

    @Test
    @DisplayName("should reset current streak after gap but keep longest")
    void shouldResetStreakAfterGap() {
        LocalDateTime day1 = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 5, 2, 10, 0);
        LocalDateTime day4 = LocalDateTime.of(2026, 5, 4, 10, 0);

        progress.updateStreak(day1);
        progress.updateStreak(day2);
        progress.setLastActivityAt(day2);
        progress.updateStreak(day4);

        assertThat(progress.getCurrentStreak()).isEqualTo(1);
        assertThat(progress.getLongestStreak()).isEqualTo(2);
    }
}
