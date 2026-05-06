package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.DifficultyLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizResultDto(
        UUID id,
        UUID quizId,
        int totalQuestions,
        int correctAnswers,
        double totalScore,
        double maxPossibleScore,
        double percentage,
        boolean passed,
        int maxStreak,
        double averageTimePerQuestion,
        DifficultyLevel difficultyReached,
        LocalDateTime completedAt
) {}
