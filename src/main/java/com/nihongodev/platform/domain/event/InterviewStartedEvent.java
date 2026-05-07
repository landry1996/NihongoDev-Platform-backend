package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewStartedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        UUID sessionId,
        String interviewType,
        String difficulty
) implements DomainEvent {

    public static InterviewStartedEvent of(UUID userId, UUID sessionId, String interviewType, String difficulty) {
        return new InterviewStartedEvent(UUID.randomUUID(), "INTERVIEW_STARTED", userId, LocalDateTime.now(),
                sessionId, interviewType, difficulty);
    }
}
