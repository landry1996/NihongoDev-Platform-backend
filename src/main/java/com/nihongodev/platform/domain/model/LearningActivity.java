package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LearningActivity {

    private UUID id;
    private UUID userId;
    private ActivityType activityType;
    private UUID referenceId;
    private Double score;
    private int xpEarned;
    private Map<String, Object> metadata;
    private LocalDateTime occurredAt;

    public static LearningActivity create(UUID userId, ActivityType activityType,
                                          UUID referenceId, Double score, int xpEarned) {
        LearningActivity activity = new LearningActivity();
        activity.id = UUID.randomUUID();
        activity.userId = userId;
        activity.activityType = activityType;
        activity.referenceId = referenceId;
        activity.score = score;
        activity.xpEarned = xpEarned;
        activity.metadata = new HashMap<>();
        activity.occurredAt = LocalDateTime.now();
        return activity;
    }

    public void addMetadata(String key, Object value) {
        if (metadata == null) metadata = new HashMap<>();
        metadata.put(key, value);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public UUID getReferenceId() { return referenceId; }
    public void setReferenceId(UUID referenceId) { this.referenceId = referenceId; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
