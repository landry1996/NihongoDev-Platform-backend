package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubmitInterviewAnswerRequest(
        @NotNull(message = "Question ID is required")
        UUID questionId,

        @NotBlank(message = "Answer text is required")
        String answerText,

        int timeSpentSeconds
) {}
