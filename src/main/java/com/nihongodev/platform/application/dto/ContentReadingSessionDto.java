package com.nihongodev.platform.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ContentReadingSessionDto(
    UUID id,
    UUID contentId,
    String contentTitle,
    int readingTimeSeconds,
    int annotationsViewed,
    int vocabularyLookedUp,
    double comprehensionScore,
    List<UUID> savedVocabulary,
    boolean completed,
    LocalDateTime startedAt,
    LocalDateTime completedAt
) {}
