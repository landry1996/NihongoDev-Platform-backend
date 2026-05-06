package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.Size;

public record UpdateLessonRequest(
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        String description,

        String type,

        String level,

        String content,

        Integer orderIndex
) {}
