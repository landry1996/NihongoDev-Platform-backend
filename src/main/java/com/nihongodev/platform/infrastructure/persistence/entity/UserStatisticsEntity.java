package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_statistics")
public class UserStatisticsEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "average_score_7_days", nullable = false)
    private double averageScore7Days;

    @Column(name = "average_score_30_days", nullable = false)
    private double averageScore30Days;

    @Column(name = "average_score_all_time", nullable = false)
    private double averageScoreAllTime;

    @Column(name = "learning_velocity", nullable = false)
    private double learningVelocity;

    @Column(name = "consistency_rate", nullable = false)
    private double consistencyRate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weak_areas", columnDefinition = "jsonb")
    private String weakAreas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommendations", columnDefinition = "jsonb")
    private String recommendations;

    @Column(name = "progress_trend", nullable = false, length = 20)
    private String progressTrend;

    @Column(name = "last_calculated_at", nullable = false)
    private LocalDateTime lastCalculatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public double getAverageScore7Days() { return averageScore7Days; }
    public void setAverageScore7Days(double v) { this.averageScore7Days = v; }
    public double getAverageScore30Days() { return averageScore30Days; }
    public void setAverageScore30Days(double v) { this.averageScore30Days = v; }
    public double getAverageScoreAllTime() { return averageScoreAllTime; }
    public void setAverageScoreAllTime(double v) { this.averageScoreAllTime = v; }
    public double getLearningVelocity() { return learningVelocity; }
    public void setLearningVelocity(double v) { this.learningVelocity = v; }
    public double getConsistencyRate() { return consistencyRate; }
    public void setConsistencyRate(double v) { this.consistencyRate = v; }
    public String getWeakAreas() { return weakAreas; }
    public void setWeakAreas(String v) { this.weakAreas = v; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String v) { this.recommendations = v; }
    public String getProgressTrend() { return progressTrend; }
    public void setProgressTrend(String v) { this.progressTrend = v; }
    public LocalDateTime getLastCalculatedAt() { return lastCalculatedAt; }
    public void setLastCalculatedAt(LocalDateTime v) { this.lastCalculatedAt = v; }
}
