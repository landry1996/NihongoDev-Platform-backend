package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.WeaknessPatternEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaWeaknessPatternRepository extends JpaRepository<WeaknessPatternEntity, UUID> {
    List<WeaknessPatternEntity> findByUserId(UUID userId);
    Optional<WeaknessPatternEntity> findByUserIdAndCategoryAndPatternDescription(
            UUID userId, String category, String patternDescription);
}
