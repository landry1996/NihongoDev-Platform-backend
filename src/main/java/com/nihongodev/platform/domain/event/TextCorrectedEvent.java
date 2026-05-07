package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record TextCorrectedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        UUID sessionId,
        String textType,
        double overallScore,
        int totalAnnotations,
        int errorCount
) implements DomainEvent {

    public static TextCorrectedEvent of(UUID userId, UUID sessionId, String textType,
                                        double overallScore, int totalAnnotations, int errorCount) {
        return new TextCorrectedEvent(UUID.randomUUID(), "TEXT_CORRECTED", userId, LocalDateTime.now(),
                sessionId, textType, overallScore, totalAnnotations, errorCount);
    }
}
