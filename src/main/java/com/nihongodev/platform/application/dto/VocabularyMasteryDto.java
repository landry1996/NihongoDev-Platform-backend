package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.MasteryLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record VocabularyMasteryDto(
        UUID id,
        UUID vocabularyId,
        MasteryLevel masteryLevel,
        double easeFactor,
        int intervalDays,
        int repetitions,
        LocalDateTime nextReviewAt,
        int correctCount,
        int incorrectCount
) {}
