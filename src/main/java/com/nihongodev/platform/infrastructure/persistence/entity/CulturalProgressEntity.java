package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cultural_progress")
public class CulturalProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(length = 50)
    private String category;

    @Column(name = "scenarios_completed")
    private int scenariosCompleted;

    @Column(name = "total_score")
    private int totalScore;

    @Column(name = "average_score")
    private int averageScore;

    @Column(name = "best_score")
    private int bestScore;

    @Column(name = "current_streak")
    private int currentStreak;

    @Column(name = "unlocked_level", length = 50)
    private String unlockedLevel;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @PrePersist
    protected void onCreate() {
        if (lastActivityAt == null) lastActivityAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
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
    public String getUnlockedLevel() { return unlockedLevel; }
    public void setUnlockedLevel(String unlockedLevel) { this.unlockedLevel = unlockedLevel; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
