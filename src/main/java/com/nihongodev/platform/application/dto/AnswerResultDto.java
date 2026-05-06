package com.nihongodev.platform.application.dto;

import java.util.UUID;

public record AnswerResultDto(
        UUID questionId,
        boolean correct,
        double pointsEarned,
        String correctAnswer,
        String explanation,
        int currentStreak,
        double streakMultiplier,
        boolean gameOver
) {}
