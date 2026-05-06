package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CorrectionSessionRepositoryPort;
import com.nihongodev.platform.domain.model.CorrectionSession;
import com.nihongodev.platform.infrastructure.persistence.mapper.CorrectionSessionPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCorrectionSessionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrectionSessionRepositoryAdapter implements CorrectionSessionRepositoryPort {

    private final JpaCorrectionSessionRepository repository;
    private final CorrectionSessionPersistenceMapper mapper;

    public CorrectionSessionRepositoryAdapter(JpaCorrectionSessionRepository repository,
                                              CorrectionSessionPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CorrectionSession save(CorrectionSession session) {
        var entity = mapper.toEntity(session);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CorrectionSession> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CorrectionSession> findByUserId(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public int countByUserId(UUID userId) {
        return repository.countByUserId(userId);
    }
}
