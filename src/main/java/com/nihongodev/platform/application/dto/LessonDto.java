package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonDto(
        UUID id,
        String title,
        String description,
        LessonType type,
        LessonLevel level,
        String content,
        int orderIndex,
        boolean published,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
