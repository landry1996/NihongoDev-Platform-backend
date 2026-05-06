package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_sessions")
public class InterviewSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "interview_type", nullable = false, length = 50)
    private String interviewType;

    @Column(nullable = false, length = 50)
    private String difficulty;

    @Column(name = "current_phase", nullable = false, length = 50)
    private String currentPhase;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "current_question_index")
    private int currentQuestionIndex;

    @Column(name = "total_questions")
    private int totalQuestions;

    @Column(name = "language_score")
    private double languageScore;

    @Column(name = "technical_score")
    private double technicalScore;

    @Column(name = "communication_score")
    private double communicationScore;

    @Column(name = "cultural_score")
    private double culturalScore;

    @Column(name = "overall_score")
    private double overallScore;

    @Column(name = "total_time_spent_seconds")
    private int totalTimeSpentSeconds;

    private boolean passed;

    @Column(name = "question_ids", columnDefinition = "TEXT")
    private String questionIds;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) startedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getCurrentPhase() { return currentPhase; }
    public void setCurrentPhase(String currentPhase) { this.currentPhase = currentPhase; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(int currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public double getLanguageScore() { return languageScore; }
    public void setLanguageScore(double languageScore) { this.languageScore = languageScore; }
    public double getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(double technicalScore) { this.technicalScore = technicalScore; }
    public double getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(double communicationScore) { this.communicationScore = communicationScore; }
    public double getCulturalScore() { return culturalScore; }
    public void setCulturalScore(double culturalScore) { this.culturalScore = culturalScore; }
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public int getTotalTimeSpentSeconds() { return totalTimeSpentSeconds; }
    public void setTotalTimeSpentSeconds(int totalTimeSpentSeconds) { this.totalTimeSpentSeconds = totalTimeSpentSeconds; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public String getQuestionIds() { return questionIds; }
    public void setQuestionIds(String questionIds) { this.questionIds = questionIds; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
