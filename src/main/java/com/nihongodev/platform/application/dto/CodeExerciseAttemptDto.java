package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CodeExerciseAttemptDto(
    UUID id,
    UUID exerciseId,
    ExerciseType exerciseType,
    String userResponse,
    CodeExerciseScoreDto score,
    List<TechnicalJapaneseViolation> violations,
    CommitMessageAnalysisDto commitAnalysis,
    String feedback,
    int timeSpentSeconds,
    LocalDateTime attemptedAt
) {}
