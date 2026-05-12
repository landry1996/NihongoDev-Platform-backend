package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CompleteReadingSessionRequest(
    @Min(0) int readingTimeSeconds,
    @Min(0) int annotationsViewed,
    @Min(0) int vocabularyLookedUp,
    @Min(0) @Max(100) double comprehensionScore
) {}
