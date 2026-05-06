package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AddQuestionRequest(
        @NotNull(message = "Quiz ID is required")
        UUID quizId,

        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "Correct answer is required")
        String correctAnswer,

        String explanation,

        String questionType,

        String difficultyLevel,

        List<String> options,

        int points,

        int timeLimitSeconds,

        int orderIndex
) {}
