package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserStatistics {

    private UUID id;
    private UUID userId;
    private double averageScore7Days;
    private double averageScore30Days;
    private double averageScoreAllTime;
    private double learningVelocity;
    private double consistencyRate;
    private List<WeakArea> weakAreas;
    private List<Recommendation> recommendations;
    private Trend progressTrend;
    private LocalDateTime lastCalculatedAt;

    public static UserStatistics initialize(UUID userId) {
        UserStatistics stats = new UserStatistics();
        stats.id = UUID.randomUUID();
        stats.userId = userId;
        stats.averageScore7Days = 0;
        stats.averageScore30Days = 0;
        stats.averageScoreAllTime = 0;
        stats.learningVelocity = 0;
        stats.consistencyRate = 0;
        stats.weakAreas = new ArrayList<>();
        stats.recommendations = new ArrayList<>();
        stats.progressTrend = Trend.STABLE;
        stats.lastCalculatedAt = LocalDateTime.now();
        return stats;
    }

    public void recalculate(List<LearningActivity> recentActivities, int daysActive7, int daysActive30) {
        calculateAverages(recentActivities);
        calculateVelocity(recentActivities);
        calculateConsistency(daysActive7, daysActive30);
        calculateTrend();
        identifyWeakAreas(recentActivities);
        generateRecommendations();
        lastCalculatedAt = LocalDateTime.now();
    }

    private void calculateAverages(List<LearningActivity> activities) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        List<LearningActivity> scored = activities.stream()
                .filter(a -> a.getScore() != null && a.getScore() > 0)
                .toList();

        averageScoreAllTime = scored.stream()
                .mapToDouble(LearningActivity::getScore)
                .average().orElse(0);

        averageScore30Days = scored.stream()
                .filter(a -> a.getOccurredAt().isAfter(thirtyDaysAgo))
                .mapToDouble(LearningActivity::getScore)
                .average().orElse(0);

        averageScore7Days = scored.stream()
                .filter(a -> a.getOccurredAt().isAfter(sevenDaysAgo))
                .mapToDouble(LearningActivity::getScore)
                .average().orElse(0);
    }

    private void calculateVelocity(List<LearningActivity> activities) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long recentCount = activities.stream()
                .filter(a -> a.getOccurredAt().isAfter(sevenDaysAgo))
                .count();
        learningVelocity = recentCount / 7.0;
    }

    private void calculateConsistency(int daysActive7, int daysActive30) {
        consistencyRate = daysActive30 > 0 ? (daysActive30 / 30.0) * 100 : 0;
    }

    private void calculateTrend() {
        progressTrend = Trend.calculate(averageScore7Days, averageScore30Days);
    }

    private void identifyWeakAreas(List<LearningActivity> activities) {
        weakAreas = new ArrayList<>();
        for (ModuleType moduleType : ModuleType.values()) {
            ActivityType activityType;
            try {
                activityType = ActivityType.fromModuleType(moduleType);
            } catch (IllegalArgumentException e) {
                continue;
            }

            List<LearningActivity> moduleActivities = activities.stream()
                    .filter(a -> a.getActivityType() == activityType && a.getScore() != null)
                    .toList();

            if (moduleActivities.size() >= 3) {
                double avg = moduleActivities.stream()
                        .mapToDouble(LearningActivity::getScore)
                        .average().orElse(0);
                if (avg < 75) {
                    weakAreas.add(WeakArea.identify(moduleType, moduleType.name().toLowerCase(), avg));
                }
            }
        }
    }

    private void generateRecommendations() {
        recommendations = new ArrayList<>();
        for (WeakArea weakArea : weakAreas) {
            recommendations.add(Recommendation.fromWeakArea(weakArea));
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public double getAverageScore7Days() { return averageScore7Days; }
    public void setAverageScore7Days(double averageScore7Days) { this.averageScore7Days = averageScore7Days; }
    public double getAverageScore30Days() { return averageScore30Days; }
    public void setAverageScore30Days(double averageScore30Days) { this.averageScore30Days = averageScore30Days; }
    public double getAverageScoreAllTime() { return averageScoreAllTime; }
    public void setAverageScoreAllTime(double averageScoreAllTime) { this.averageScoreAllTime = averageScoreAllTime; }
    public double getLearningVelocity() { return learningVelocity; }
    public void setLearningVelocity(double learningVelocity) { this.learningVelocity = learningVelocity; }
    public double getConsistencyRate() { return consistencyRate; }
    public void setConsistencyRate(double consistencyRate) { this.consistencyRate = consistencyRate; }
    public List<WeakArea> getWeakAreas() { return weakAreas; }
    public void setWeakAreas(List<WeakArea> weakAreas) { this.weakAreas = weakAreas; }
    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
    public Trend getProgressTrend() { return progressTrend; }
    public void setProgressTrend(Trend progressTrend) { this.progressTrend = progressTrend; }
    public LocalDateTime getLastCalculatedAt() { return lastCalculatedAt; }
    public void setLastCalculatedAt(LocalDateTime lastCalculatedAt) { this.lastCalculatedAt = lastCalculatedAt; }
}
