package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.domain.model.Badge;
import com.nihongodev.platform.domain.model.BadgeCategory;
import com.nihongodev.platform.infrastructure.persistence.mapper.BadgePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaBadgeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BadgeRepositoryAdapter implements BadgeRepositoryPort {

    private final JpaBadgeRepository jpaRepository;
    private final BadgePersistenceMapper mapper;

    public BadgeRepositoryAdapter(JpaBadgeRepository jpaRepository,
                                   BadgePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Badge save(Badge badge) {
        var entity = mapper.toEntity(badge);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Badge> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Badge> findByCode(String code) {
        return jpaRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<Badge> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Badge> findByCategory(BadgeCategory category) {
        return jpaRepository.findByCategory(category.name()).stream()
            .map(mapper::toDomain).toList();
    }

    @Override
    public List<Badge> findByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByIdIn(ids).stream().map(mapper::toDomain).toList();
    }
}
