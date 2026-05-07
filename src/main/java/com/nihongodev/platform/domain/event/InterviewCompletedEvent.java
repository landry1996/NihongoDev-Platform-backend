package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewCompletedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        UUID sessionId,
        String interviewType,
        double overallScore,
        int durationSeconds,
        boolean passed
) implements DomainEvent {

    public static InterviewCompletedEvent of(UUID userId, UUID sessionId, String interviewType,
                                             double overallScore, int durationSeconds, boolean passed) {
        return new InterviewCompletedEvent(UUID.randomUUID(), "INTERVIEW_COMPLETED", userId, LocalDateTime.now(),
                sessionId, interviewType, overallScore, durationSeconds, passed);
    }
}
