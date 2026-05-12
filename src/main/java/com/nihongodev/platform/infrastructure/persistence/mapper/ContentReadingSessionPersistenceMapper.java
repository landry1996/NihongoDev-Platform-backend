package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.ContentReadingSession;
import com.nihongodev.platform.infrastructure.persistence.entity.ContentReadingSessionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ContentReadingSessionPersistenceMapper {

    public ContentReadingSession toDomain(ContentReadingSessionEntity entity) {
        ContentReadingSession session = new ContentReadingSession();
        session.setId(entity.getId());
        session.setUserId(entity.getUserId());
        session.setContentId(entity.getContentId());
        session.setReadingTimeSeconds(entity.getReadingTimeSeconds());
        session.setAnnotationsViewed(entity.getAnnotationsViewed());
        session.setVocabularyLookedUp(entity.getVocabularyLookedUp());
        session.setComprehensionScore(entity.getComprehensionScore());
        session.setCompleted(entity.isCompleted());
        session.setStartedAt(entity.getStartedAt());
        session.setCompletedAt(entity.getCompletedAt());

        if (entity.getSavedVocabulary() != null) {
            List<UUID> savedVocab = entity.getSavedVocabulary().stream()
                .map(UUID::fromString)
                .toList();
            session.setSavedVocabulary(new ArrayList<>(savedVocab));
        }

        return session;
    }

    public ContentReadingSessionEntity toEntity(ContentReadingSession session) {
        ContentReadingSessionEntity entity = new ContentReadingSessionEntity();
        entity.setId(session.getId());
        entity.setUserId(session.getUserId());
        entity.setContentId(session.getContentId());
        entity.setReadingTimeSeconds(session.getReadingTimeSeconds());
        entity.setAnnotationsViewed(session.getAnnotationsViewed());
        entity.setVocabularyLookedUp(session.getVocabularyLookedUp());
        entity.setComprehensionScore(session.getComprehensionScore());
        entity.setCompleted(session.isCompleted());
        entity.setStartedAt(session.getStartedAt());
        entity.setCompletedAt(session.getCompletedAt());

        if (session.getSavedVocabulary() != null) {
            List<String> savedVocab = session.getSavedVocabulary().stream()
                .map(UUID::toString)
                .toList();
            entity.setSavedVocabulary(savedVocab);
        }

        return entity;
    }
}
