package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ExerciseType;
import com.nihongodev.platform.domain.model.ViolationType;

import java.time.LocalDateTime;
import java.util.Map;

public record CodeJapaneseProgressDto(
    ExerciseType exerciseType,
    int exercisesCompleted,
    int averageScore,
    int bestScore,
    int currentStreak,
    Map<ViolationType, Integer> recurringViolations,
    LocalDateTime lastActivityAt
) {}
