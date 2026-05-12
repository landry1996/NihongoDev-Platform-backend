package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "code_exercise_attempts")
public class CodeExerciseAttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "exercise_id", nullable = false)
    private UUID exerciseId;

    @Column(name = "exercise_type", nullable = false, length = 30)
    private String exerciseType;

    @Column(name = "user_response", nullable = false, columnDefinition = "TEXT")
    private String userResponse;

    @Column(name = "technical_accuracy_score", nullable = false)
    private int technicalAccuracyScore;

    @Column(name = "japanese_quality_score", nullable = false)
    private int japaneseQualityScore;

    @Column(name = "professional_tone_score", nullable = false)
    private int professionalToneScore;

    @Column(name = "structure_score", nullable = false)
    private int structureScore;

    @Column(name = "team_communication_score", nullable = false)
    private int teamCommunicationScore;

    @Column(name = "overall_score", nullable = false)
    private int overallScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String violations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "commit_analysis", columnDefinition = "jsonb")
    private Map<String, Object> commitAnalysis;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "time_spent_seconds", nullable = false)
    private int timeSpentSeconds;

    @Column(name = "attempted_at", nullable = false)
    private LocalDateTime attemptedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getExerciseId() { return exerciseId; }
    public void setExerciseId(UUID exerciseId) { this.exerciseId = exerciseId; }
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
    public String getUserResponse() { return userResponse; }
    public void setUserResponse(String userResponse) { this.userResponse = userResponse; }
    public int getTechnicalAccuracyScore() { return technicalAccuracyScore; }
    public void setTechnicalAccuracyScore(int technicalAccuracyScore) { this.technicalAccuracyScore = technicalAccuracyScore; }
    public int getJapaneseQualityScore() { return japaneseQualityScore; }
    public void setJapaneseQualityScore(int japaneseQualityScore) { this.japaneseQualityScore = japaneseQualityScore; }
    public int getProfessionalToneScore() { return professionalToneScore; }
    public void setProfessionalToneScore(int professionalToneScore) { this.professionalToneScore = professionalToneScore; }
    public int getStructureScore() { return structureScore; }
    public void setStructureScore(int structureScore) { this.structureScore = structureScore; }
    public int getTeamCommunicationScore() { return teamCommunicationScore; }
    public void setTeamCommunicationScore(int teamCommunicationScore) { this.teamCommunicationScore = teamCommunicationScore; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public String getViolations() { return violations; }
    public void setViolations(String violations) { this.violations = violations; }
    public Map<String, Object> getCommitAnalysis() { return commitAnalysis; }
    public void setCommitAnalysis(Map<String, Object> commitAnalysis) { this.commitAnalysis = commitAnalysis; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}
