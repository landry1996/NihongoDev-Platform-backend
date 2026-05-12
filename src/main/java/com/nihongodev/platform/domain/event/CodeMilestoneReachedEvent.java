package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.ExerciseType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CodeMilestoneReachedEvent(
    UUID eventId,
    String eventType,
    UUID userId,
    ExerciseType exerciseType,
    int exercisesCompleted,
    int averageScore,
    LocalDateTime occurredAt
) implements DomainEvent {
    public static CodeMilestoneReachedEvent create(UUID userId, ExerciseType type, int completed, int avgScore) {
        return new CodeMilestoneReachedEvent(
            UUID.randomUUID(), "CODE_MILESTONE_REACHED", userId, type, completed, avgScore, LocalDateTime.now()
        );
    }
}
