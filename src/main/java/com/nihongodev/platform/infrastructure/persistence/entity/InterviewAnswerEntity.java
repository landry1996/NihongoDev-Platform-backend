package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_answers")
public class InterviewAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "time_spent_seconds")
    private int timeSpentSeconds;

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

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
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
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
