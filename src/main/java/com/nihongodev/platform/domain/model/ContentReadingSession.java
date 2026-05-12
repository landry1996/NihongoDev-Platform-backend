package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContentReadingSession {

    private UUID id;
    private UUID userId;
    private UUID contentId;
    private int readingTimeSeconds;
    private int annotationsViewed;
    private int vocabularyLookedUp;
    private double comprehensionScore;
    private List<UUID> savedVocabulary;
    private boolean completed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public ContentReadingSession() {
        this.savedVocabulary = new ArrayList<>();
    }

    public static ContentReadingSession start(UUID userId, UUID contentId) {
        ContentReadingSession session = new ContentReadingSession();
        session.id = UUID.randomUUID();
        session.userId = userId;
        session.contentId = contentId;
        session.readingTimeSeconds = 0;
        session.annotationsViewed = 0;
        session.vocabularyLookedUp = 0;
        session.completed = false;
        session.startedAt = LocalDateTime.now();
        return session;
    }

    public void complete(int readingTimeSeconds, int annotationsViewed,
                         int vocabularyLookedUp, double comprehensionScore) {
        this.readingTimeSeconds = readingTimeSeconds;
        this.annotationsViewed = annotationsViewed;
        this.vocabularyLookedUp = vocabularyLookedUp;
        this.comprehensionScore = comprehensionScore;
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    public void saveVocabulary(UUID annotationId) {
        if (!this.savedVocabulary.contains(annotationId)) {
            this.savedVocabulary.add(annotationId);
        }
    }

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
    public List<UUID> getSavedVocabulary() { return savedVocabulary; }
    public void setSavedVocabulary(List<UUID> savedVocabulary) { this.savedVocabulary = savedVocabulary; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
