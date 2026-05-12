package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.ContentReadingSessionRepositoryPort;
import com.nihongodev.platform.domain.model.ContentReadingSession;
import com.nihongodev.platform.infrastructure.persistence.mapper.ContentReadingSessionPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaContentReadingSessionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ContentReadingSessionRepositoryAdapter implements ContentReadingSessionRepositoryPort {

    private final JpaContentReadingSessionRepository jpaRepository;
    private final ContentReadingSessionPersistenceMapper mapper;

    public ContentReadingSessionRepositoryAdapter(JpaContentReadingSessionRepository jpaRepository,
                                                   ContentReadingSessionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ContentReadingSession save(ContentReadingSession session) {
        var entity = mapper.toEntity(session);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ContentReadingSession> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ContentReadingSession> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<ContentReadingSession> findByUserIdAndContentId(UUID userId, UUID contentId) {
        return jpaRepository.findByUserIdAndContentId(userId, contentId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<ContentReadingSession> findActiveByUserIdAndContentId(UUID userId, UUID contentId) {
        return jpaRepository.findActiveByUserIdAndContentId(userId, contentId).map(mapper::toDomain);
    }
}
