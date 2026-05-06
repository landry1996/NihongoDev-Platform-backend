package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateQuizRequest(
        UUID lessonId,

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        String description,

        String level,

        String mode,

        int timeLimitSeconds,

        int maxAttempts,

        int passingScore
) {}
