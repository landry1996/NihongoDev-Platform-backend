package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.domain.model.InterviewSession;
import com.nihongodev.platform.infrastructure.persistence.mapper.InterviewSessionPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaInterviewSessionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InterviewSessionRepositoryAdapter implements InterviewSessionRepositoryPort {

    private final JpaInterviewSessionRepository jpaRepository;
    private final InterviewSessionPersistenceMapper mapper;

    public InterviewSessionRepositoryAdapter(JpaInterviewSessionRepository jpaRepository,
                                             InterviewSessionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public InterviewSession save(InterviewSession session) {
        var entity = mapper.toEntity(session);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<InterviewSession> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<InterviewSession> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
