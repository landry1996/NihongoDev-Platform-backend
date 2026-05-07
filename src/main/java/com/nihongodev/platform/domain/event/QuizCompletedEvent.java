package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizCompletedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        UUID quizId,
        UUID attemptId,
        String quizTitle,
        double percentage,
        boolean passed,
        int maxStreak,
        String mode
) implements DomainEvent {

    public static QuizCompletedEvent of(UUID userId, UUID quizId, UUID attemptId, String quizTitle,
                                        double percentage, boolean passed, int maxStreak, String mode) {
        return new QuizCompletedEvent(UUID.randomUUID(), "QUIZ_COMPLETED", userId, LocalDateTime.now(),
                quizId, attemptId, quizTitle, percentage, passed, maxStreak, mode);
    }
}
