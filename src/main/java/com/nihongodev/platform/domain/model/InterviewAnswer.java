package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class InterviewAnswer {

    private UUID id;
    private UUID sessionId;
    private UUID questionId;
    private String answerText;
    private int timeSpentSeconds;
    private double languageScore;
    private double technicalScore;
    private double communicationScore;
    private double culturalScore;
    private double overallScore;
    private LocalDateTime submittedAt;

    public InterviewAnswer() {}

    public static InterviewAnswer submit(UUID sessionId, UUID questionId, String answerText, int timeSpentSeconds) {
        InterviewAnswer answer = new InterviewAnswer();
        answer.id = UUID.randomUUID();
        answer.sessionId = sessionId;
        answer.questionId = questionId;
        answer.answerText = answerText;
        answer.timeSpentSeconds = timeSpentSeconds;
        answer.languageScore = 0;
        answer.technicalScore = 0;
        answer.communicationScore = 0;
        answer.culturalScore = 0;
        answer.overallScore = 0;
        answer.submittedAt = LocalDateTime.now();
        return answer;
    }

    public void applyScores(double language, double technical, double communication, double cultural) {
        this.languageScore = language;
        this.technicalScore = technical;
        this.communicationScore = communication;
        this.culturalScore = cultural;
        this.overallScore = (language + technical + communication + cultural) / 4.0;
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
