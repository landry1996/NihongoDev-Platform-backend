package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CulturalProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCulturalProgressRepository extends JpaRepository<CulturalProgressEntity, UUID> {
    Optional<CulturalProgressEntity> findByUserIdAndCategory(UUID userId, String category);
    List<CulturalProgressEntity> findByUserId(UUID userId);
}
