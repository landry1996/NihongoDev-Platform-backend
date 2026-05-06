package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.QuizMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuizDto(
        UUID id,
        UUID lessonId,
        String title,
        String description,
        String level,
        QuizMode mode,
        int timeLimitSeconds,
        int maxAttempts,
        int passingScore,
        boolean published,
        int questionCount,
        List<QuestionDto> questions,
        LocalDateTime createdAt
) {}
