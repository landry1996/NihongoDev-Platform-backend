package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VocabularyQuizGeneratedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        String quizType,
        int wordCount,
        String category,
        String level,
        List<UUID> vocabularyIds
) implements DomainEvent {

    public static VocabularyQuizGeneratedEvent of(UUID userId, String quizType, int wordCount,
                                                   String category, String level, List<UUID> vocabularyIds) {
        return new VocabularyQuizGeneratedEvent(UUID.randomUUID(), "VOCABULARY_QUIZ_GENERATED", userId,
                LocalDateTime.now(), quizType, wordCount, category, level, vocabularyIds);
    }
}
