package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenerateLlmCvRequest(
        @NotBlank(message = "pitchType is required")
        String pitchType,

        String targetCompanyType,

        @Size(max = 500, message = "Additional instructions must not exceed 500 characters")
        String additionalInstructions
) {}
