package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.model.UserProgress;
import com.nihongodev.platform.infrastructure.persistence.mapper.UserProgressPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserProgressRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProgressRepositoryAdapter implements ProgressRepositoryPort {

    private final JpaUserProgressRepository jpaRepository;
    private final UserProgressPersistenceMapper mapper;

    public ProgressRepositoryAdapter(JpaUserProgressRepository jpaRepository, UserProgressPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public UserProgress save(UserProgress progress) {
        var entity = mapper.toEntity(progress);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserProgress> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }
}
