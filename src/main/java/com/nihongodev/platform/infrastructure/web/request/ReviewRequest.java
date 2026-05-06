package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReviewRequest(
        @NotNull(message = "Vocabulary ID is required")
        UUID vocabularyId,

        boolean correct
) {}
