package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SubmitInterviewAnswerRequest(
        @NotNull(message = "Question ID is required")
        UUID questionId,

        @NotBlank(message = "Answer text is required")
        @Size(max = 3000, message = "Answer text must not exceed 3000 characters")
        String answerText,

        int timeSpentSeconds
) {}
