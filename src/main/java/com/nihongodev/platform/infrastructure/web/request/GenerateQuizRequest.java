package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GenerateQuizRequest(
        String quizType,
        String category,
        String level,

        @Min(value = 4, message = "Minimum 4 words")
        @Max(value = 50, message = "Maximum 50 words")
        int wordCount
) {}
