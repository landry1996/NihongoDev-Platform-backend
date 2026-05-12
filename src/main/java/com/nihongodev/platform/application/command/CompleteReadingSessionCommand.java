package com.nihongodev.platform.application.command;

import java.util.UUID;

public record CompleteReadingSessionCommand(
    UUID userId,
    UUID sessionId,
    int readingTimeSeconds,
    int annotationsViewed,
    int vocabularyLookedUp,
    double comprehensionScore
) {}
