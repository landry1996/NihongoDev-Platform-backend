package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewStartedEvent(
        UUID userId,
        UUID sessionId,
        String interviewType,
        String difficulty,
        LocalDateTime startedAt
) {
    public static InterviewStartedEvent of(UUID userId, UUID sessionId, String interviewType,
                                           String difficulty, LocalDateTime startedAt) {
        return new InterviewStartedEvent(userId, sessionId, interviewType, difficulty, startedAt);
    }
}
