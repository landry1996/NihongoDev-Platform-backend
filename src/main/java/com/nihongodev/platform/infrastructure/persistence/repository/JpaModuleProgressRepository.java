package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.ModuleProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaModuleProgressRepository extends JpaRepository<ModuleProgressEntity, UUID> {
    Optional<ModuleProgressEntity> findByUserIdAndModuleType(UUID userId, String moduleType);
    List<ModuleProgressEntity> findAllByUserId(UUID userId);
}
