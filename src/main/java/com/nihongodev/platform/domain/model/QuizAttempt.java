package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuizAttempt {

    private UUID id;
    private UUID quizId;
    private UUID userId;
    private QuizMode mode;
    private AttemptStatus status;
    private int currentStreak;
    private int maxStreak;
    private int livesRemaining;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private int timeSpentSeconds;

    public QuizAttempt() {}

    public static QuizAttempt start(UUID quizId, UUID userId, QuizMode mode) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.id = UUID.randomUUID();
        attempt.quizId = quizId;
        attempt.userId = userId;
        attempt.mode = mode != null ? mode : QuizMode.CLASSIC;
        attempt.status = AttemptStatus.IN_PROGRESS;
        attempt.currentStreak = 0;
        attempt.maxStreak = 0;
        attempt.livesRemaining = mode == QuizMode.SURVIVAL ? 3 : -1;
        attempt.startedAt = LocalDateTime.now();
        attempt.timeSpentSeconds = 0;
        return attempt;
    }

    public void recordCorrectAnswer() {
        this.currentStreak++;
        if (this.currentStreak > this.maxStreak) {
            this.maxStreak = this.currentStreak;
        }
    }

    public void recordIncorrectAnswer() {
        this.currentStreak = 0;
        if (this.mode == QuizMode.SURVIVAL && this.livesRemaining > 0) {
            this.livesRemaining--;
            if (this.livesRemaining == 0) {
                this.status = AttemptStatus.GAME_OVER;
                this.completedAt = LocalDateTime.now();
            }
        }
    }

    public void complete(int totalTimeSeconds) {
        this.status = AttemptStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.timeSpentSeconds = totalTimeSeconds;
    }

    public boolean isGameOver() {
        return this.status == AttemptStatus.GAME_OVER;
    }

    public boolean isInProgress() {
        return this.status == AttemptStatus.IN_PROGRESS;
    }

    public double getStreakMultiplier() {
        if (currentStreak < 2) return 1.0;
        if (currentStreak < 4) return 1.5;
        if (currentStreak < 6) return 2.0;
        if (currentStreak < 8) return 2.5;
        return 3.0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getQuizId() { return quizId; }
    public void setQuizId(UUID quizId) { this.quizId = quizId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public QuizMode getMode() { return mode; }
    public void setMode(QuizMode mode) { this.mode = mode; }
    public AttemptStatus getStatus() { return status; }
    public void setStatus(AttemptStatus status) { this.status = status; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getMaxStreak() { return maxStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }
    public int getLivesRemaining() { return livesRemaining; }
    public void setLivesRemaining(int livesRemaining) { this.livesRemaining = livesRemaining; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
}
