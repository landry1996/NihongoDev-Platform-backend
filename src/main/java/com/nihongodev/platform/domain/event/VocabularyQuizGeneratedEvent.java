package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VocabularyQuizGeneratedEvent(
        UUID userId,
        String quizType,
        int wordCount,
        String category,
        String level,
        List<UUID> vocabularyIds,
        LocalDateTime occurredAt
) {
    public static VocabularyQuizGeneratedEvent of(UUID userId, String quizType, int wordCount,
                                                   String category, String level, List<UUID> vocabularyIds) {
        return new VocabularyQuizGeneratedEvent(userId, quizType, wordCount, category, level, vocabularyIds, LocalDateTime.now());
    }
}
