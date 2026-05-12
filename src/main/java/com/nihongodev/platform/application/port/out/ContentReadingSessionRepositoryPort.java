package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.ContentReadingSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentReadingSessionRepositoryPort {
    ContentReadingSession save(ContentReadingSession session);
    Optional<ContentReadingSession> findById(UUID id);
    List<ContentReadingSession> findByUserId(UUID userId);
    List<ContentReadingSession> findByUserIdAndContentId(UUID userId, UUID contentId);
    Optional<ContentReadingSession> findActiveByUserIdAndContentId(UUID userId, UUID contentId);
}
