package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubmitAnswerRequest(
        @NotNull(message = "Question ID is required")
        UUID questionId,

        @NotBlank(message = "Answer is required")
        String userAnswer,

        int timeSpentSeconds
) {}
