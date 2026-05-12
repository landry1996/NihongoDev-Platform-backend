package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ContentReadingSession")
class ContentReadingSessionTest {

    @Test
    @DisplayName("should start session with correct initial state")
    void shouldStartCorrectly() {
        UUID userId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();

        ContentReadingSession session = ContentReadingSession.start(userId, contentId);

        assertThat(session.getId()).isNotNull();
        assertThat(session.getUserId()).isEqualTo(userId);
        assertThat(session.getContentId()).isEqualTo(contentId);
        assertThat(session.isCompleted()).isFalse();
        assertThat(session.getStartedAt()).isNotNull();
        assertThat(session.getReadingTimeSeconds()).isEqualTo(0);
    }

    @Test
    @DisplayName("should complete session with metrics")
    void shouldComplete() {
        ContentReadingSession session = ContentReadingSession.start(UUID.randomUUID(), UUID.randomUUID());

        session.complete(300, 10, 5, 85.0);

        assertThat(session.isCompleted()).isTrue();
        assertThat(session.getReadingTimeSeconds()).isEqualTo(300);
        assertThat(session.getAnnotationsViewed()).isEqualTo(10);
        assertThat(session.getVocabularyLookedUp()).isEqualTo(5);
        assertThat(session.getComprehensionScore()).isEqualTo(85.0);
        assertThat(session.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("should save vocabulary without duplicates")
    void shouldSaveVocabularyNoDuplicates() {
        ContentReadingSession session = ContentReadingSession.start(UUID.randomUUID(), UUID.randomUUID());
        UUID annotationId = UUID.randomUUID();

        session.saveVocabulary(annotationId);
        session.saveVocabulary(annotationId);

        assertThat(session.getSavedVocabulary()).hasSize(1);
    }

    @Test
    @DisplayName("should save multiple distinct vocabulary items")
    void shouldSaveMultipleVocabulary() {
        ContentReadingSession session = ContentReadingSession.start(UUID.randomUUID(), UUID.randomUUID());

        session.saveVocabulary(UUID.randomUUID());
        session.saveVocabulary(UUID.randomUUID());
        session.saveVocabulary(UUID.randomUUID());

        assertThat(session.getSavedVocabulary()).hasSize(3);
    }
}
