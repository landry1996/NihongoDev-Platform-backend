package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.infrastructure.persistence.mapper.ModuleProgressPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaModuleProgressRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ModuleProgressRepositoryAdapter implements ModuleProgressRepositoryPort {

    private final JpaModuleProgressRepository jpaRepository;
    private final ModuleProgressPersistenceMapper mapper;

    public ModuleProgressRepositoryAdapter(JpaModuleProgressRepository jpaRepository, ModuleProgressPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ModuleProgress save(ModuleProgress moduleProgress) {
        var entity = mapper.toEntity(moduleProgress);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ModuleProgress> findByUserIdAndModuleType(UUID userId, ModuleType moduleType) {
        return jpaRepository.findByUserIdAndModuleType(userId, moduleType.name()).map(mapper::toDomain);
    }

    @Override
    public List<ModuleProgress> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
