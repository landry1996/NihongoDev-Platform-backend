package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CulturalProgressRepositoryPort;
import com.nihongodev.platform.domain.model.CulturalProgress;
import com.nihongodev.platform.domain.model.ScenarioCategory;
import com.nihongodev.platform.infrastructure.persistence.mapper.CulturalProgressPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCulturalProgressRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CulturalProgressRepositoryAdapter implements CulturalProgressRepositoryPort {

    private final JpaCulturalProgressRepository repository;
    private final CulturalProgressPersistenceMapper mapper;

    public CulturalProgressRepositoryAdapter(JpaCulturalProgressRepository repository,
                                              CulturalProgressPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CulturalProgress save(CulturalProgress progress) {
        var entity = mapper.toEntity(progress);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CulturalProgress> findByUserIdAndCategory(UUID userId, ScenarioCategory category) {
        return repository.findByUserIdAndCategory(userId, category.name())
                .map(mapper::toDomain);
    }

    @Override
    public List<CulturalProgress> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
