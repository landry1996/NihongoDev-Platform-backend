package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InterviewSession {

    private UUID id;
    private UUID userId;
    private InterviewType interviewType;
    private InterviewDifficulty difficulty;
    private InterviewPhase currentPhase;
    private SessionStatus status;
    private int currentQuestionIndex;
    private int totalQuestions;
    private double languageScore;
    private double technicalScore;
    private double communicationScore;
    private double culturalScore;
    private double overallScore;
    private int totalTimeSpentSeconds;
    private boolean passed;
    private List<UUID> questionIds;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public InterviewSession() {
        this.questionIds = new ArrayList<>();
    }

    public static InterviewSession start(UUID userId, InterviewType type, InterviewDifficulty difficulty, int totalQuestions) {
        InterviewSession session = new InterviewSession();
        session.id = UUID.randomUUID();
        session.userId = userId;
        session.interviewType = type;
        session.difficulty = difficulty;
        session.currentPhase = InterviewPhase.INTRODUCTION;
        session.status = SessionStatus.IN_PROGRESS;
        session.currentQuestionIndex = 0;
        session.totalQuestions = totalQuestions;
        session.languageScore = 0;
        session.technicalScore = 0;
        session.communicationScore = 0;
        session.culturalScore = 0;
        session.overallScore = 0;
        session.totalTimeSpentSeconds = 0;
        session.passed = false;
        session.questionIds = new ArrayList<>();
        session.startedAt = LocalDateTime.now();
        return session;
    }

    public void advanceQuestion() {
        this.currentQuestionIndex++;
        updatePhase();
    }

    private void updatePhase() {
        if (totalQuestions == 0) return;
        double progress = (double) currentQuestionIndex / totalQuestions;
        if (progress < 0.15) this.currentPhase = InterviewPhase.INTRODUCTION;
        else if (progress < 0.75) this.currentPhase = InterviewPhase.MAIN_QUESTIONS;
        else if (progress < 0.9) this.currentPhase = InterviewPhase.FOLLOW_UP;
        else this.currentPhase = InterviewPhase.CLOSING;
    }

    public void addScore(double language, double technical, double communication, double cultural) {
        int answered = currentQuestionIndex;
        this.languageScore = ((this.languageScore * (answered - 1)) + language) / answered;
        this.technicalScore = ((this.technicalScore * (answered - 1)) + technical) / answered;
        this.communicationScore = ((this.communicationScore * (answered - 1)) + communication) / answered;
        this.culturalScore = ((this.culturalScore * (answered - 1)) + cultural) / answered;
        this.overallScore = (this.languageScore + this.technicalScore + this.communicationScore + this.culturalScore) / 4.0;
    }

    public void addTimeSpent(int seconds) {
        this.totalTimeSpentSeconds += seconds;
    }

    public void complete() {
        this.status = SessionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.passed = this.overallScore >= 60.0;
    }

    public void abandon() {
        this.status = SessionStatus.ABANDONED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isInProgress() {
        return this.status == SessionStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this.status == SessionStatus.COMPLETED;
    }

    public boolean hasMoreQuestions() {
        return this.currentQuestionIndex < this.totalQuestions;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public InterviewType getInterviewType() { return interviewType; }
    public void setInterviewType(InterviewType interviewType) { this.interviewType = interviewType; }
    public InterviewDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(InterviewDifficulty difficulty) { this.difficulty = difficulty; }
    public InterviewPhase getCurrentPhase() { return currentPhase; }
    public void setCurrentPhase(InterviewPhase currentPhase) { this.currentPhase = currentPhase; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
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
    public List<UUID> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<UUID> questionIds) { this.questionIds = questionIds; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
