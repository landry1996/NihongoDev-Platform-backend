package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.AttemptStatus;
import com.nihongodev.platform.domain.model.QuizMode;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizAttemptDto(
        UUID id,
        UUID quizId,
        QuizMode mode,
        AttemptStatus status,
        int currentStreak,
        int maxStreak,
        int livesRemaining,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {}
