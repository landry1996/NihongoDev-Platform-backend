package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ModuleProgress {

    private UUID id;
    private UUID userId;
    private ModuleType moduleType;
    private int completedItems;
    private Integer totalItems;
    private double averageScore;
    private double bestScore;
    private LocalDateTime lastCompletedAt;
    private ModuleStatus status;

    public static ModuleProgress initialize(UUID userId, ModuleType moduleType) {
        ModuleProgress mp = new ModuleProgress();
        mp.id = UUID.randomUUID();
        mp.userId = userId;
        mp.moduleType = moduleType;
        mp.completedItems = 0;
        mp.averageScore = 0;
        mp.bestScore = 0;
        mp.status = ModuleStatus.NOT_STARTED;
        return mp;
    }

    public void recordCompletion(double score) {
        if (status == ModuleStatus.NOT_STARTED) {
            status = ModuleStatus.IN_PROGRESS;
        }
        double totalScore = averageScore * completedItems;
        completedItems++;
        averageScore = (totalScore + score) / completedItems;
        if (score > bestScore) {
            bestScore = score;
        }
        lastCompletedAt = LocalDateTime.now();
    }

    public double getCompletionPercentage() {
        if (totalItems == null || totalItems == 0) return 0;
        return ((double) completedItems / totalItems) * 100;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ModuleType getModuleType() { return moduleType; }
    public void setModuleType(ModuleType moduleType) { this.moduleType = moduleType; }
    public int getCompletedItems() { return completedItems; }
    public void setCompletedItems(int completedItems) { this.completedItems = completedItems; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public double getBestScore() { return bestScore; }
    public void setBestScore(double bestScore) { this.bestScore = bestScore; }
    public LocalDateTime getLastCompletedAt() { return lastCompletedAt; }
    public void setLastCompletedAt(LocalDateTime lastCompletedAt) { this.lastCompletedAt = lastCompletedAt; }
    public ModuleStatus getStatus() { return status; }
    public void setStatus(ModuleStatus status) { this.status = status; }
}
