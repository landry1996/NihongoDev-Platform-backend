package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateVocabularyRequest(
        String french,

        String english,

        @NotBlank(message = "Japanese word is required")
        String japanese,

        String romaji,

        String example,

        String codeExample,

        @NotNull(message = "Category is required")
        String category,

        String level,

        String domain,

        List<String> tags,

        UUID lessonId
) {}
