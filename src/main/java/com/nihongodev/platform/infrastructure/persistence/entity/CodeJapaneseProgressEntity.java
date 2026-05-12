package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "code_japanese_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "exercise_type"})
})
public class CodeJapaneseProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "exercise_type", nullable = false, length = 30)
    private String exerciseType;

    @Column(name = "exercises_completed", nullable = false)
    private int exercisesCompleted;

    @Column(name = "total_score", nullable = false)
    private int totalScore;

    @Column(name = "average_score", nullable = false)
    private int averageScore;

    @Column(name = "best_score", nullable = false)
    private int bestScore;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recurring_violations", columnDefinition = "jsonb")
    private Map<String, Integer> recurringViolations;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
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
    public Map<String, Integer> getRecurringViolations() { return recurringViolations; }
    public void setRecurringViolations(Map<String, Integer> recurringViolations) { this.recurringViolations = recurringViolations; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
