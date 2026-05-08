package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scenario_attempts")
public class ScenarioAttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "scenario_id")
    private UUID scenarioId;

    @Column(name = "user_response", columnDefinition = "TEXT")
    private String userResponse;

    @Column(name = "selected_choice_id")
    private UUID selectedChoiceId;

    @Column(name = "keigo_score")
    private int keigoScore;

    @Column(name = "appropriateness_score")
    private int appropriatenessScore;

    @Column(name = "uchi_soto_score")
    private int uchiSotoScore;

    @Column(name = "indirectness_score")
    private int indirectnessScore;

    @Column(name = "professional_tone_score")
    private int professionalToneScore;

    @Column(name = "overall_score")
    private int overallScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String violations;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "time_spent_seconds")
    private int timeSpentSeconds;

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

    @PrePersist
    protected void onCreate() {
        if (attemptedAt == null) attemptedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getScenarioId() { return scenarioId; }
    public void setScenarioId(UUID scenarioId) { this.scenarioId = scenarioId; }
    public String getUserResponse() { return userResponse; }
    public void setUserResponse(String userResponse) { this.userResponse = userResponse; }
    public UUID getSelectedChoiceId() { return selectedChoiceId; }
    public void setSelectedChoiceId(UUID selectedChoiceId) { this.selectedChoiceId = selectedChoiceId; }
    public int getKeigoScore() { return keigoScore; }
    public void setKeigoScore(int keigoScore) { this.keigoScore = keigoScore; }
    public int getAppropriatenessScore() { return appropriatenessScore; }
    public void setAppropriatenessScore(int appropriatenessScore) { this.appropriatenessScore = appropriatenessScore; }
    public int getUchiSotoScore() { return uchiSotoScore; }
    public void setUchiSotoScore(int uchiSotoScore) { this.uchiSotoScore = uchiSotoScore; }
    public int getIndirectnessScore() { return indirectnessScore; }
    public void setIndirectnessScore(int indirectnessScore) { this.indirectnessScore = indirectnessScore; }
    public int getProfessionalToneScore() { return professionalToneScore; }
    public void setProfessionalToneScore(int professionalToneScore) { this.professionalToneScore = professionalToneScore; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public String getViolations() { return violations; }
    public void setViolations(String violations) { this.violations = violations; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}
