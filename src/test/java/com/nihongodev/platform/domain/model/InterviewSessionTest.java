package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InterviewSession Domain Model")
class InterviewSessionTest {

    @Test
    @DisplayName("should start with correct initial state")
    void shouldStartWithCorrectState() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 5);

        assertThat(session.getId()).isNotNull();
        assertThat(session.getStatus()).isEqualTo(SessionStatus.IN_PROGRESS);
        assertThat(session.getCurrentPhase()).isEqualTo(InterviewPhase.INTRODUCTION);
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(0);
        assertThat(session.getTotalQuestions()).isEqualTo(5);
        assertThat(session.getOverallScore()).isEqualTo(0);
        assertThat(session.isInProgress()).isTrue();
        assertThat(session.hasMoreQuestions()).isTrue();
    }

    @Test
    @DisplayName("should advance question index and update phase")
    void shouldAdvanceQuestion() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.TECH_JAVA,
                InterviewDifficulty.INTERMEDIATE, 10);

        session.advanceQuestion();
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(1);
        assertThat(session.getCurrentPhase()).isEqualTo(InterviewPhase.INTRODUCTION);

        session.advanceQuestion();
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(2);
        assertThat(session.getCurrentPhase()).isEqualTo(InterviewPhase.MAIN_QUESTIONS);
    }

    @Test
    @DisplayName("should transition through phases correctly")
    void shouldTransitionPhases() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.BEHAVIORAL,
                InterviewDifficulty.ADVANCED, 10);

        // 0-14% = INTRODUCTION
        session.advanceQuestion(); // index 1 = 10%
        assertThat(session.getCurrentPhase()).isEqualTo(InterviewPhase.INTRODUCTION);

        session.advanceQuestion(); // index 2 = 20%
        assertThat(session.getCurrentPhase()).isEqualTo(InterviewPhase.MAIN_QUESTIONS);

        // Advance to FOLLOW_UP range (75-89%)
        for (int i = 0; i < 6; i++) session.advanceQuestion();
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(8);
        assertThat(session.getCurrentPhase()).isEqualTo(InterviewPhase.FOLLOW_UP);

        // Advance to CLOSING (90%+)
        session.advanceQuestion(); // index 9 = 90%
        assertThat(session.getCurrentPhase()).isEqualTo(InterviewPhase.CLOSING);
    }

    @Test
    @DisplayName("should calculate running average scores")
    void shouldCalculateRunningAverageScores() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 5);

        session.advanceQuestion();
        session.addScore(80, 70, 60, 50);
        assertThat(session.getLanguageScore()).isEqualTo(80);

        session.advanceQuestion();
        session.addScore(60, 90, 80, 70);
        assertThat(session.getLanguageScore()).isEqualTo(70); // (80+60)/2
    }

    @Test
    @DisplayName("should calculate overall score from 4 dimensions")
    void shouldCalculateOverallScore() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.TECH_SPRING,
                InterviewDifficulty.INTERMEDIATE, 3);

        session.advanceQuestion();
        session.addScore(80, 80, 80, 80);

        assertThat(session.getOverallScore()).isEqualTo(80.0);
    }

    @Test
    @DisplayName("should complete session and set passed flag")
    void shouldCompleteSession() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.BUSINESS_JAPANESE,
                InterviewDifficulty.INTERMEDIATE, 3);
        session.advanceQuestion();
        session.addScore(70, 80, 65, 60);

        session.complete();

        assertThat(session.getStatus()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(session.getCompletedAt()).isNotNull();
        assertThat(session.isPassed()).isTrue();
        assertThat(session.isInProgress()).isFalse();
    }

    @Test
    @DisplayName("should mark as not passed when score < 60")
    void shouldNotPassLowScore() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.TECH_AWS,
                InterviewDifficulty.ADVANCED, 3);
        session.advanceQuestion();
        session.addScore(40, 30, 50, 45);

        session.complete();

        assertThat(session.isPassed()).isFalse();
    }

    @Test
    @DisplayName("should abandon session correctly")
    void shouldAbandonSession() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 5);

        session.abandon();

        assertThat(session.getStatus()).isEqualTo(SessionStatus.ABANDONED);
        assertThat(session.getCompletedAt()).isNotNull();
        assertThat(session.isInProgress()).isFalse();
    }

    @Test
    @DisplayName("should track total time spent")
    void shouldTrackTimeSpent() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.TECH_JAVA,
                InterviewDifficulty.BEGINNER, 3);

        session.addTimeSpent(45);
        session.addTimeSpent(60);
        session.addTimeSpent(30);

        assertThat(session.getTotalTimeSpentSeconds()).isEqualTo(135);
    }

    @Test
    @DisplayName("should detect when no more questions")
    void shouldDetectNoMoreQuestions() {
        InterviewSession session = InterviewSession.start(UUID.randomUUID(), InterviewType.SELF_INTRODUCTION,
                InterviewDifficulty.BEGINNER, 2);

        assertThat(session.hasMoreQuestions()).isTrue();
        session.advanceQuestion();
        assertThat(session.hasMoreQuestions()).isTrue();
        session.advanceQuestion();
        assertThat(session.hasMoreQuestions()).isFalse();
    }
}
