package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CodeJapaneseProgress {

    private UUID id;
    private UUID userId;
    private ExerciseType exerciseType;
    private int exercisesCompleted;
    private int totalScore;
    private int averageScore;
    private int bestScore;
    private int currentStreak;
    private Map<ViolationType, Integer> recurringViolations;
    private LocalDateTime lastActivityAt;

    public CodeJapaneseProgress() {}

    public static CodeJapaneseProgress create(UUID userId, ExerciseType exerciseType) {
        CodeJapaneseProgress progress = new CodeJapaneseProgress();
        progress.id = UUID.randomUUID();
        progress.userId = userId;
        progress.exerciseType = exerciseType;
        progress.exercisesCompleted = 0;
        progress.totalScore = 0;
        progress.averageScore = 0;
        progress.bestScore = 0;
        progress.currentStreak = 0;
        progress.lastActivityAt = LocalDateTime.now();
        return progress;
    }

    public void recordAttempt(int score) {
        this.exercisesCompleted++;
        this.totalScore += score;
        this.averageScore = this.totalScore / this.exercisesCompleted;
        if (score > this.bestScore) {
            this.bestScore = score;
        }
        if (score >= 60) {
            this.currentStreak++;
        } else {
            this.currentStreak = 0;
        }
        this.lastActivityAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ExerciseType getExerciseType() { return exerciseType; }
    public void setExerciseType(ExerciseType exerciseType) { this.exerciseType = exerciseType; }
    public int getExercisesCompleted() { return exercisesCompleted; }
    public void setExercisesCompleted(int exercisesCompleted) { this.exercisesCompleted = exercisesCompleted; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getAverageScore() { return averageScore; }
    public void setAverageScore(int averageScore) { this.averageScore = averageScore; }
    public int getBestScore() { return bestScore; }
    public void setBestScore(int bestScore) { this.bestScore = bestScore; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public Map<ViolationType, Integer> getRecurringViolations() { return recurringViolations; }
    public void setRecurringViolations(Map<ViolationType, Integer> recurringViolations) { this.recurringViolations = recurringViolations; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
