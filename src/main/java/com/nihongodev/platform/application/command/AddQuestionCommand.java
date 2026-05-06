package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.DifficultyLevel;
import com.nihongodev.platform.domain.model.QuestionType;

import java.util.List;
import java.util.UUID;

public record AddQuestionCommand(
        UUID quizId,
        String content,
        String correctAnswer,
        String explanation,
        QuestionType questionType,
        DifficultyLevel difficultyLevel,
        List<String> options,
        int points,
        int timeLimitSeconds,
        int orderIndex
) {}
