package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.QuizMode;

import java.util.UUID;

public record CreateQuizCommand(
        UUID lessonId,
        String title,
        String description,
        String level,
        QuizMode mode,
        int timeLimitSeconds,
        int maxAttempts,
        int passingScore
) {}
