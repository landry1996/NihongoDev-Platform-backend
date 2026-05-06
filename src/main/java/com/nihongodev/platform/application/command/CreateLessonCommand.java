package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;

public record CreateLessonCommand(
        String title,
        String description,
        LessonType type,
        LessonLevel level,
        String content,
        int orderIndex
) {}
