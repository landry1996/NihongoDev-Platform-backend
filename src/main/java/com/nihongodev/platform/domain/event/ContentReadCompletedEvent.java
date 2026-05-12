package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContentReadCompletedEvent(
    UUID eventId,
    String eventType,
    UUID userId,
    UUID contentId,
    UUID sessionId,
    int readingTimeSeconds,
    double comprehensionScore,
    int vocabularySaved,
    LocalDateTime occurredAt
) implements DomainEvent {
    public static ContentReadCompletedEvent create(UUID userId, UUID contentId, UUID sessionId,
                                                   int readingTimeSeconds, double comprehensionScore,
                                                   int vocabularySaved) {
        return new ContentReadCompletedEvent(
            UUID.randomUUID(), "CONTENT_READ_COMPLETED", userId, contentId, sessionId,
            readingTimeSeconds, comprehensionScore, vocabularySaved, LocalDateTime.now()
        );
    }
}
