package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CorrectTextRequest(
        @NotBlank(message = "Text is required")
        @Size(max = 5000, message = "Text must not exceed 5000 characters")
        String text,

        String textType,

        String targetLevel
) {}
