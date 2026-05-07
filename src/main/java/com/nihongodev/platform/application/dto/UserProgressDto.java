package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ProgressLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserProgressDto(
        UUID userId,
        int totalLessonsCompleted,
        int totalQuizzesCompleted,
        int totalInterviewsCompleted,
        int totalCorrectionsCompleted,
        double globalScore,
        int currentStreak,
        int longestStreak,
        ProgressLevel level,
        long totalXp,
        LocalDateTime lastActivityAt
) {}
