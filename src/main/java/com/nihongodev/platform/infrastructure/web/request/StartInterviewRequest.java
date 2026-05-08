package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StartInterviewRequest(
        @NotBlank(message = "Interview type is required")
        String interviewType,

        @NotNull(message = "Difficulty is required")
        String difficulty
) {}
