package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;

public record StartInterviewRequest(
        @NotBlank(message = "Interview type is required")
        String interviewType,

        String difficulty
) {}
