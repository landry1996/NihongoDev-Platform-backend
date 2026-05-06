package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;

import java.util.UUID;

public record UpdateLessonCommand(
        UUID lessonId,
        String title,
        String description,
        LessonType type,
        LessonLevel level,
        String content,
        Integer orderIndex
) {}
