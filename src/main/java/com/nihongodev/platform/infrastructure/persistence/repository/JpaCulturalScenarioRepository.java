package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CulturalScenarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaCulturalScenarioRepository extends JpaRepository<CulturalScenarioEntity, UUID> {

    @Query("SELECT s FROM CulturalScenarioEntity s WHERE s.published = true " +
            "AND (:context IS NULL OR s.context = :context) " +
            "AND (:difficulty IS NULL OR s.difficulty = :difficulty) " +
            "AND (:category IS NULL OR s.category = :category)")
    List<CulturalScenarioEntity> findPublished(
            @Param("context") String context,
            @Param("difficulty") String difficulty,
            @Param("category") String category);
}
