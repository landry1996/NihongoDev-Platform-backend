package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "content_reading_sessions")
public class ContentReadingSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "reading_time_seconds")
    private int readingTimeSeconds;

    @Column(name = "annotations_viewed")
    private int annotationsViewed;

    @Column(name = "vocabulary_looked_up")
    private int vocabularyLookedUp;

    @Column(name = "comprehension_score")
    private double comprehensionScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "saved_vocabulary", columnDefinition = "jsonb")
    private List<String> savedVocabulary;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getContentId() { return contentId; }
    public void setContentId(UUID contentId) { this.contentId = contentId; }
    public int getReadingTimeSeconds() { return readingTimeSeconds; }
    public void setReadingTimeSeconds(int readingTimeSeconds) { this.readingTimeSeconds = readingTimeSeconds; }
    public int getAnnotationsViewed() { return annotationsViewed; }
    public void setAnnotationsViewed(int annotationsViewed) { this.annotationsViewed = annotationsViewed; }
    public int getVocabularyLookedUp() { return vocabularyLookedUp; }
    public void setVocabularyLookedUp(int vocabularyLookedUp) { this.vocabularyLookedUp = vocabularyLookedUp; }
    public double getComprehensionScore() { return comprehensionScore; }
    public void setComprehensionScore(double comprehensionScore) { this.comprehensionScore = comprehensionScore; }
    public List<String> getSavedVocabulary() { return savedVocabulary; }
    public void setSavedVocabulary(List<String> savedVocabulary) { this.savedVocabulary = savedVocabulary; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
