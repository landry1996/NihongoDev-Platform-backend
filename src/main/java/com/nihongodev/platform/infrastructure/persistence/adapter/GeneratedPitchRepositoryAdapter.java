package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.infrastructure.persistence.mapper.GeneratedPitchPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaGeneratedPitchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class GeneratedPitchRepositoryAdapter implements GeneratedPitchRepositoryPort {

    private final JpaGeneratedPitchRepository repository;
    private final GeneratedPitchPersistenceMapper mapper;

    public GeneratedPitchRepositoryAdapter(JpaGeneratedPitchRepository repository,
                                           GeneratedPitchPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public GeneratedPitch save(GeneratedPitch pitch) {
        var entity = mapper.toEntity(pitch);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<GeneratedPitch> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<GeneratedPitch> findByUserIdAndPitchType(UUID userId, PitchType pitchType) {
        return repository.findByUserIdAndPitchTypeOrderByGeneratedAtDesc(userId, pitchType.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<GeneratedPitch> findLatestByUserIdAndPitchType(UUID userId, PitchType pitchType) {
        return repository.findFirstByUserIdAndPitchTypeOrderByGeneratedAtDesc(userId, pitchType.name())
                .map(mapper::toDomain);
    }
}
