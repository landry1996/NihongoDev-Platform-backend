package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizCompletedEvent(
        UUID userId,
        UUID quizId,
        UUID attemptId,
        String quizTitle,
        double percentage,
        boolean passed,
        int maxStreak,
        String mode,
        LocalDateTime occurredAt
) {
    public static QuizCompletedEvent of(UUID userId, UUID quizId, UUID attemptId, String quizTitle,
                                        double percentage, boolean passed, int maxStreak, String mode) {
        return new QuizCompletedEvent(userId, quizId, attemptId, quizTitle, percentage, passed, maxStreak, mode, LocalDateTime.now());
    }
}
