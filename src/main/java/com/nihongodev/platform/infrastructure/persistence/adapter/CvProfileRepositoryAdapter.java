package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.infrastructure.persistence.mapper.CvProfilePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCvProfileRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CvProfileRepositoryAdapter implements CvProfileRepositoryPort {

    private final JpaCvProfileRepository repository;
    private final CvProfilePersistenceMapper mapper;

    public CvProfileRepositoryAdapter(JpaCvProfileRepository repository, CvProfilePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CvProfile save(CvProfile profile) {
        var entity = mapper.toEntity(profile);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CvProfile> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return repository.existsByUserId(userId);
    }
}
