package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class VocabularyMastery {

    private UUID id;
    private UUID userId;
    private UUID vocabularyId;
    private MasteryLevel masteryLevel;
    private double easeFactor;
    private int intervalDays;
    private int repetitions;
    private LocalDateTime nextReviewAt;
    private LocalDateTime lastReviewedAt;
    private int correctCount;
    private int incorrectCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VocabularyMastery() {}

    public static VocabularyMastery create(UUID userId, UUID vocabularyId) {
        VocabularyMastery m = new VocabularyMastery();
        m.id = UUID.randomUUID();
        m.userId = userId;
        m.vocabularyId = vocabularyId;
        m.masteryLevel = MasteryLevel.NEW;
        m.easeFactor = 2.5;
        m.intervalDays = 0;
        m.repetitions = 0;
        m.correctCount = 0;
        m.incorrectCount = 0;
        m.nextReviewAt = LocalDateTime.now();
        m.createdAt = LocalDateTime.now();
        m.updatedAt = LocalDateTime.now();
        return m;
    }

    public void recordReview(boolean correct) {
        this.lastReviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (correct) {
            this.correctCount++;
            this.repetitions++;

            if (repetitions == 1) {
                this.intervalDays = 1;
            } else if (repetitions == 2) {
                this.intervalDays = 6;
            } else {
                this.intervalDays = (int) Math.round(this.intervalDays * this.easeFactor);
            }

            this.easeFactor = Math.max(1.3, this.easeFactor + 0.1 - (5 - 4) * (0.08 + (5 - 4) * 0.02));
        } else {
            this.incorrectCount++;
            this.repetitions = 0;
            this.intervalDays = 1;
            this.easeFactor = Math.max(1.3, this.easeFactor - 0.2);
        }

        this.nextReviewAt = this.lastReviewedAt.plusDays(this.intervalDays);
        this.masteryLevel = calculateMasteryLevel();
    }

    private MasteryLevel calculateMasteryLevel() {
        if (repetitions == 0) return MasteryLevel.NEW;
        if (repetitions < 3) return MasteryLevel.LEARNING;
        if (intervalDays >= 21) return MasteryLevel.MASTERED;
        return MasteryLevel.REVIEWING;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getVocabularyId() { return vocabularyId; }
    public void setVocabularyId(UUID vocabularyId) { this.vocabularyId = vocabularyId; }
    public MasteryLevel getMasteryLevel() { return masteryLevel; }
    public void setMasteryLevel(MasteryLevel masteryLevel) { this.masteryLevel = masteryLevel; }
    public double getEaseFactor() { return easeFactor; }
    public void setEaseFactor(double easeFactor) { this.easeFactor = easeFactor; }
    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    public LocalDateTime getNextReviewAt() { return nextReviewAt; }
    public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }
    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; }
    public void setLastReviewedAt(LocalDateTime lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public int getIncorrectCount() { return incorrectCount; }
    public void setIncorrectCount(int incorrectCount) { this.incorrectCount = incorrectCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
