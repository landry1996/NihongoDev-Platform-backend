package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonCompletedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        UUID lessonId,
        String lessonTitle,
        String lessonType,
        String lessonLevel
) implements DomainEvent {

    public static LessonCompletedEvent of(UUID userId, UUID lessonId, String lessonTitle,
                                          String lessonType, String lessonLevel) {
        return new LessonCompletedEvent(UUID.randomUUID(), "LESSON_COMPLETED", userId, LocalDateTime.now(),
                lessonId, lessonTitle, lessonType, lessonLevel);
    }
}
