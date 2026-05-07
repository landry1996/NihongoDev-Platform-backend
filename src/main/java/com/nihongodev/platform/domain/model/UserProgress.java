package com.nihongodev.platform.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserProgress {

    private UUID id;
    private UUID userId;
    private int totalLessonsCompleted;
    private int totalQuizzesCompleted;
    private int totalInterviewsCompleted;
    private int totalCorrectionsCompleted;
    private double globalScore;
    private int currentStreak;
    private int longestStreak;
    private LocalDateTime lastActivityAt;
    private ProgressLevel level;
    private long totalXp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int scoredActivitiesCount;
    private double weightedScoreSum;
    private double weightSum;

    public static UserProgress initialize(UUID userId) {
        UserProgress progress = new UserProgress();
        progress.id = UUID.randomUUID();
        progress.userId = userId;
        progress.level = ProgressLevel.BEGINNER;
        progress.totalXp = 0;
        progress.totalLessonsCompleted = 0;
        progress.totalQuizzesCompleted = 0;
        progress.totalInterviewsCompleted = 0;
        progress.totalCorrectionsCompleted = 0;
        progress.globalScore = 0;
        progress.currentStreak = 0;
        progress.longestStreak = 0;
        progress.scoredActivitiesCount = 0;
        progress.weightedScoreSum = 0;
        progress.weightSum = 0;
        progress.createdAt = LocalDateTime.now();
        progress.updatedAt = LocalDateTime.now();
        return progress;
    }

    public void recordActivity(ActivityType activityType, double score) {
        switch (activityType) {
            case LESSON_COMPLETED -> totalLessonsCompleted++;
            case QUIZ_COMPLETED -> totalQuizzesCompleted++;
            case INTERVIEW_COMPLETED -> totalInterviewsCompleted++;
            case CORRECTION_COMPLETED -> totalCorrectionsCompleted++;
        }
        int xpEarned = activityType.calculateXp(score);
        totalXp += xpEarned;
        level = ProgressLevel.fromXp(totalXp);
        updatedAt = LocalDateTime.now();
    }

    public void updateGlobalScore(ActivityType activityType, double score) {
        double weight = activityType.getScoreWeight();
        weightedScoreSum += score * weight;
        weightSum += weight;
        scoredActivitiesCount++;
        if (weightSum > 0) {
            globalScore = weightedScoreSum / weightSum;
        }
    }

    public void updateStreak(LocalDateTime activityTime) {
        LocalDate activityDate = activityTime.toLocalDate();

        if (lastActivityAt == null) {
            currentStreak = 1;
            longestStreak = 1;
            lastActivityAt = activityTime;
            return;
        }

        LocalDate lastDate = lastActivityAt.toLocalDate();
        if (activityDate.equals(lastDate)) {
            return;
        }

        if (activityDate.equals(lastDate.plusDays(1))) {
            currentStreak++;
        } else {
            currentStreak = 1;
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }
        lastActivityAt = activityTime;
    }

    public void resetStreak() {
        currentStreak = 0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public int getTotalLessonsCompleted() { return totalLessonsCompleted; }
    public void setTotalLessonsCompleted(int totalLessonsCompleted) { this.totalLessonsCompleted = totalLessonsCompleted; }
    public int getTotalQuizzesCompleted() { return totalQuizzesCompleted; }
    public void setTotalQuizzesCompleted(int totalQuizzesCompleted) { this.totalQuizzesCompleted = totalQuizzesCompleted; }
    public int getTotalInterviewsCompleted() { return totalInterviewsCompleted; }
    public void setTotalInterviewsCompleted(int totalInterviewsCompleted) { this.totalInterviewsCompleted = totalInterviewsCompleted; }
    public int getTotalCorrectionsCompleted() { return totalCorrectionsCompleted; }
    public void setTotalCorrectionsCompleted(int totalCorrectionsCompleted) { this.totalCorrectionsCompleted = totalCorrectionsCompleted; }
    public double getGlobalScore() { return globalScore; }
    public void setGlobalScore(double globalScore) { this.globalScore = globalScore; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public ProgressLevel getLevel() { return level; }
    public void setLevel(ProgressLevel level) { this.level = level; }
    public long getTotalXp() { return totalXp; }
    public void setTotalXp(long totalXp) { this.totalXp = totalXp; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public int getScoredActivitiesCount() { return scoredActivitiesCount; }
    public void setScoredActivitiesCount(int scoredActivitiesCount) { this.scoredActivitiesCount = scoredActivitiesCount; }
    public double getWeightedScoreSum() { return weightedScoreSum; }
    public void setWeightedScoreSum(double weightedScoreSum) { this.weightedScoreSum = weightedScoreSum; }
    public double getWeightSum() { return weightSum; }
    public void setWeightSum(double weightSum) { this.weightSum = weightSum; }
}
