package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonCompletedEvent(
        UUID userId,
        UUID lessonId,
        String lessonTitle,
        String lessonType,
        String lessonLevel,
        LocalDateTime occurredAt
) {
    public static LessonCompletedEvent of(UUID userId, UUID lessonId, String lessonTitle, String lessonType, String lessonLevel) {
        return new LessonCompletedEvent(userId, lessonId, lessonTitle, lessonType, lessonLevel, LocalDateTime.now());
    }
}
