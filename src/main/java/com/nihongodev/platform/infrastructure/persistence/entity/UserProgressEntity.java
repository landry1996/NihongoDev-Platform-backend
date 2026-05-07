package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_progress")
public class UserProgressEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "total_lessons_completed", nullable = false)
    private int totalLessonsCompleted;

    @Column(name = "total_quizzes_completed", nullable = false)
    private int totalQuizzesCompleted;

    @Column(name = "total_interviews_completed", nullable = false)
    private int totalInterviewsCompleted;

    @Column(name = "total_corrections_completed", nullable = false)
    private int totalCorrectionsCompleted;

    @Column(name = "global_score", nullable = false)
    private double globalScore;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "level", nullable = false, length = 20)
    private String level;

    @Column(name = "total_xp", nullable = false)
    private long totalXp;

    @Column(name = "scored_activities_count", nullable = false)
    private int scoredActivitiesCount;

    @Column(name = "weighted_score_sum", nullable = false)
    private double weightedScoreSum;

    @Column(name = "weight_sum", nullable = false)
    private double weightSum;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public int getTotalLessonsCompleted() { return totalLessonsCompleted; }
    public void setTotalLessonsCompleted(int v) { this.totalLessonsCompleted = v; }
    public int getTotalQuizzesCompleted() { return totalQuizzesCompleted; }
    public void setTotalQuizzesCompleted(int v) { this.totalQuizzesCompleted = v; }
    public int getTotalInterviewsCompleted() { return totalInterviewsCompleted; }
    public void setTotalInterviewsCompleted(int v) { this.totalInterviewsCompleted = v; }
    public int getTotalCorrectionsCompleted() { return totalCorrectionsCompleted; }
    public void setTotalCorrectionsCompleted(int v) { this.totalCorrectionsCompleted = v; }
    public double getGlobalScore() { return globalScore; }
    public void setGlobalScore(double v) { this.globalScore = v; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int v) { this.currentStreak = v; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int v) { this.longestStreak = v; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime v) { this.lastActivityAt = v; }
    public String getLevel() { return level; }
    public void setLevel(String v) { this.level = v; }
    public long getTotalXp() { return totalXp; }
    public void setTotalXp(long v) { this.totalXp = v; }
    public int getScoredActivitiesCount() { return scoredActivitiesCount; }
    public void setScoredActivitiesCount(int v) { this.scoredActivitiesCount = v; }
    public double getWeightedScoreSum() { return weightedScoreSum; }
    public void setWeightedScoreSum(double v) { this.weightedScoreSum = v; }
    public double getWeightSum() { return weightSum; }
    public void setWeightSum(double v) { this.weightSum = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
