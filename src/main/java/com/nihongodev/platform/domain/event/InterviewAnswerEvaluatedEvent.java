package com.nihongodev.platform.domain.event;

import java.util.UUID;

public record InterviewAnswerEvaluatedEvent(
        UUID userId,
        UUID sessionId,
        UUID questionId,
        double overallScore,
        String phase
) {
    public static InterviewAnswerEvaluatedEvent of(UUID userId, UUID sessionId, UUID questionId,
                                                   double overallScore, String phase) {
        return new InterviewAnswerEvaluatedEvent(userId, sessionId, questionId, overallScore, phase);
    }
}
