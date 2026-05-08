package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class CulturalProgress {

    private UUID id;
    private UUID userId;
    private ScenarioCategory category;
    private int scenariosCompleted;
    private int totalScore;
    private int averageScore;
    private int bestScore;
    private int currentStreak;
    private KeigoLevel unlockedLevel;
    private LocalDateTime lastActivityAt;

    public CulturalProgress() {}

    public static CulturalProgress initialize(UUID userId, ScenarioCategory category) {
        CulturalProgress progress = new CulturalProgress();
        progress.id = UUID.randomUUID();
        progress.userId = userId;
        progress.category = category;
        progress.scenariosCompleted = 0;
        progress.totalScore = 0;
        progress.averageScore = 0;
        progress.bestScore = 0;
        progress.currentStreak = 0;
        progress.unlockedLevel = KeigoLevel.TEINEIGO;
        progress.lastActivityAt = LocalDateTime.now();
        return progress;
    }

    public void recordAttempt(int score) {
        this.scenariosCompleted++;
        this.totalScore += score;
        this.averageScore = this.totalScore / this.scenariosCompleted;
        if (score > this.bestScore) {
            this.bestScore = score;
        }
        if (score >= 70) {
            this.currentStreak++;
        } else {
            this.currentStreak = 0;
        }
        this.lastActivityAt = LocalDateTime.now();
        updateUnlockedLevel();
    }

    private void updateUnlockedLevel() {
        if (averageScore >= 85 && scenariosCompleted >= 20) {
            unlockedLevel = KeigoLevel.MIXED_FORMAL;
        } else if (averageScore >= 75 && scenariosCompleted >= 15) {
            unlockedLevel = KeigoLevel.KENJOUGO;
        } else if (averageScore >= 65 && scenariosCompleted >= 10) {
            unlockedLevel = KeigoLevel.SONKEIGO;
        } else if (averageScore >= 50 && scenariosCompleted >= 5) {
            unlockedLevel = KeigoLevel.TEINEIGO;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ScenarioCategory getCategory() { return category; }
    public void setCategory(ScenarioCategory category) { this.category = category; }
    public int getScenariosCompleted() { return scenariosCompleted; }
    public void setScenariosCompleted(int scenariosCompleted) { this.scenariosCompleted = scenariosCompleted; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getAverageScore() { return averageScore; }
    public void setAverageScore(int averageScore) { this.averageScore = averageScore; }
    public int getBestScore() { return bestScore; }
    public void setBestScore(int bestScore) { this.bestScore = bestScore; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public KeigoLevel getUnlockedLevel() { return unlockedLevel; }
    public void setUnlockedLevel(KeigoLevel unlockedLevel) { this.unlockedLevel = unlockedLevel; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
