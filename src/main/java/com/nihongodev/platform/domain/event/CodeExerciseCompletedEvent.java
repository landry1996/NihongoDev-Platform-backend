package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.ExerciseType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CodeExerciseCompletedEvent(
    UUID eventId,
    String eventType,
    UUID userId,
    UUID exerciseId,
    ExerciseType exerciseType,
    int overallScore,
    LocalDateTime occurredAt
) implements DomainEvent {
    public static CodeExerciseCompletedEvent create(UUID userId, UUID exerciseId, ExerciseType type, int score) {
        return new CodeExerciseCompletedEvent(
            UUID.randomUUID(), "CODE_EXERCISE_COMPLETED", userId, exerciseId, type, score, LocalDateTime.now()
        );
    }
}
