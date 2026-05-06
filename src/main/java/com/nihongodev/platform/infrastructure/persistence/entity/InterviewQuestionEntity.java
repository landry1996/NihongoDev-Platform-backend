package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "interview_type", nullable = false, length = 50)
    private String interviewType;

    @Column(nullable = false, length = 50)
    private String difficulty;

    @Column(nullable = false, length = 50)
    private String phase;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_japanese", columnDefinition = "TEXT")
    private String contentJapanese;

    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(name = "expected_keywords", columnDefinition = "TEXT")
    private String expectedKeywords;

    @Column(name = "scoring_criteria", columnDefinition = "TEXT")
    private String scoringCriteria;

    @Column(name = "time_limit_seconds")
    private int timeLimitSeconds;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentJapanese() { return contentJapanese; }
    public void setContentJapanese(String contentJapanese) { this.contentJapanese = contentJapanese; }
    public String getModelAnswer() { return modelAnswer; }
    public void setModelAnswer(String modelAnswer) { this.modelAnswer = modelAnswer; }
    public String getExpectedKeywords() { return expectedKeywords; }
    public void setExpectedKeywords(String expectedKeywords) { this.expectedKeywords = expectedKeywords; }
    public String getScoringCriteria() { return scoringCriteria; }
    public void setScoringCriteria(String scoringCriteria) { this.scoringCriteria = scoringCriteria; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(int timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
