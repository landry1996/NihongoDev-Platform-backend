package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class InterviewQuestion {

    private UUID id;
    private InterviewType interviewType;
    private InterviewDifficulty difficulty;
    private InterviewPhase phase;
    private String content;
    private String contentJapanese;
    private String modelAnswer;
    private List<String> expectedKeywords;
    private String scoringCriteria;
    private int timeLimitSeconds;
    private int orderIndex;
    private LocalDateTime createdAt;

    public InterviewQuestion() {}

    public static InterviewQuestion create(InterviewType type, InterviewDifficulty difficulty,
                                           InterviewPhase phase, String content, String contentJapanese,
                                           String modelAnswer, List<String> expectedKeywords,
                                           String scoringCriteria, int timeLimitSeconds, int orderIndex) {
        InterviewQuestion q = new InterviewQuestion();
        q.id = UUID.randomUUID();
        q.interviewType = type;
        q.difficulty = difficulty;
        q.phase = phase;
        q.content = content;
        q.contentJapanese = contentJapanese;
        q.modelAnswer = modelAnswer;
        q.expectedKeywords = expectedKeywords;
        q.scoringCriteria = scoringCriteria;
        q.timeLimitSeconds = timeLimitSeconds > 0 ? timeLimitSeconds : 120;
        q.orderIndex = orderIndex;
        q.createdAt = LocalDateTime.now();
        return q;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public InterviewType getInterviewType() { return interviewType; }
    public void setInterviewType(InterviewType interviewType) { this.interviewType = interviewType; }
    public InterviewDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(InterviewDifficulty difficulty) { this.difficulty = difficulty; }
    public InterviewPhase getPhase() { return phase; }
    public void setPhase(InterviewPhase phase) { this.phase = phase; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentJapanese() { return contentJapanese; }
    public void setContentJapanese(String contentJapanese) { this.contentJapanese = contentJapanese; }
    public String getModelAnswer() { return modelAnswer; }
    public void setModelAnswer(String modelAnswer) { this.modelAnswer = modelAnswer; }
    public List<String> getExpectedKeywords() { return expectedKeywords; }
    public void setExpectedKeywords(List<String> expectedKeywords) { this.expectedKeywords = expectedKeywords; }
    public String getScoringCriteria() { return scoringCriteria; }
    public void setScoringCriteria(String scoringCriteria) { this.scoringCriteria = scoringCriteria; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(int timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
