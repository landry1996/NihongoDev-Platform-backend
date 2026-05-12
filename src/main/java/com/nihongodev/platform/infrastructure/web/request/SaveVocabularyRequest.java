package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SaveVocabularyRequest(
    @NotNull UUID annotationId
) {}
