package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewAnswerEvaluatedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        UUID sessionId,
        UUID questionId,
        double overallScore,
        String phase
) implements DomainEvent {

    public static InterviewAnswerEvaluatedEvent of(UUID userId, UUID sessionId, UUID questionId,
                                                    double overallScore, String phase) {
        return new InterviewAnswerEvaluatedEvent(UUID.randomUUID(), "INTERVIEW_ANSWER_EVALUATED", userId,
                LocalDateTime.now(), sessionId, questionId, overallScore, phase);
    }
}
