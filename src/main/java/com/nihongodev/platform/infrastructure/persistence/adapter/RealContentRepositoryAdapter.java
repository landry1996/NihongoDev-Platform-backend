package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.mapper.RealContentPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaRealContentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RealContentRepositoryAdapter implements RealContentRepositoryPort {

    private final JpaRealContentRepository jpaRepository;
    private final RealContentPersistenceMapper mapper;

    public RealContentRepositoryAdapter(JpaRealContentRepository jpaRepository,
                                         RealContentPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RealContent save(RealContent content) {
        var entity = mapper.toEntity(content);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RealContent> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<RealContent> findByStatus(ContentStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<RealContent> findPublishedByDomainAndDifficulty(ContentDomain domain, ReadingDifficulty maxDifficulty) {
        return jpaRepository.findPublishedByDomainAndDifficulty(domain.name(), maxDifficulty.name()).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<RealContent> findPublished() {
        return jpaRepository.findPublished().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<RealContent> findRecentPublished(int limit) {
        return jpaRepository.findRecentPublished(limit).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsBySourceUrl(String sourceUrl) {
        return jpaRepository.existsBySourceUrl(sourceUrl);
    }
}
