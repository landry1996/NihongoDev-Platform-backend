package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.WeaknessPatternRepositoryPort;
import com.nihongodev.platform.domain.model.AnnotationCategory;
import com.nihongodev.platform.domain.model.WeaknessPattern;
import com.nihongodev.platform.infrastructure.persistence.mapper.WeaknessPatternPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaWeaknessPatternRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WeaknessPatternRepositoryAdapter implements WeaknessPatternRepositoryPort {

    private final JpaWeaknessPatternRepository repository;
    private final WeaknessPatternPersistenceMapper mapper;

    public WeaknessPatternRepositoryAdapter(JpaWeaknessPatternRepository repository,
                                            WeaknessPatternPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WeaknessPattern save(WeaknessPattern pattern) {
        var entity = mapper.toEntity(pattern);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<WeaknessPattern> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<WeaknessPattern> findByUserIdAndCategoryAndDescription(UUID userId,
                                                                            AnnotationCategory category,
                                                                            String description) {
        return repository.findByUserIdAndCategoryAndPatternDescription(
                userId, category.name(), description
        ).map(mapper::toDomain);
    }
}
