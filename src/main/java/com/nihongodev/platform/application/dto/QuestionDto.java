package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.DifficultyLevel;
import com.nihongodev.platform.domain.model.QuestionType;

import java.util.List;
import java.util.UUID;

public record QuestionDto(
        UUID id,
        String content,
        QuestionType questionType,
        DifficultyLevel difficultyLevel,
        List<String> options,
        int points,
        int timeLimitSeconds,
        int orderIndex
) {}
