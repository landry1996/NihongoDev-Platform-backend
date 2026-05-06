package com.nihongodev.platform.domain.event;

import java.util.UUID;

public record InterviewCompletedEvent(
        UUID userId,
        UUID sessionId,
        String interviewType,
        double overallScore,
        int durationSeconds,
        boolean passed
) {
    public static InterviewCompletedEvent of(UUID userId, UUID sessionId, String interviewType,
                                             double overallScore, int durationSeconds, boolean passed) {
        return new InterviewCompletedEvent(userId, sessionId, interviewType, overallScore, durationSeconds, passed);
    }
}
