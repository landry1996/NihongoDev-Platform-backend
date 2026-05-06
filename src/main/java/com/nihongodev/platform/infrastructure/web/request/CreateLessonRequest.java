package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLessonRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        String description,

        @NotNull(message = "Type is required")
        String type,

        @NotNull(message = "Level is required")
        String level,

        String content,

        int orderIndex
) {}
