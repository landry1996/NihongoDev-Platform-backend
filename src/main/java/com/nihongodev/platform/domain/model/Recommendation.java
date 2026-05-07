package com.nihongodev.platform.domain.model;

import java.util.UUID;

public class Recommendation {
    private RecommendationType type;
    private UUID targetId;
    private String reason;
    private Priority priority;

    public Recommendation() {}

    public Recommendation(RecommendationType type, UUID targetId, String reason, Priority priority) {
        this.type = type;
        this.targetId = targetId;
        this.reason = reason;
        this.priority = priority;
    }

    public static Recommendation fromWeakArea(WeakArea weakArea) {
        RecommendationType type = switch (weakArea.getModuleType()) {
            case LESSON -> RecommendationType.REVIEW_LESSON;
            case QUIZ -> RecommendationType.RETRY_QUIZ;
            case INTERVIEW -> RecommendationType.PRACTICE_INTERVIEW;
            case CORRECTION -> RecommendationType.PRACTICE_CORRECTION;
            default -> RecommendationType.READ_CONTENT;
        };
        return new Recommendation(type, null, "Low score in " + weakArea.getTopic(), weakArea.getPriority());
    }

    public RecommendationType getType() { return type; }
    public void setType(RecommendationType type) { this.type = type; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
