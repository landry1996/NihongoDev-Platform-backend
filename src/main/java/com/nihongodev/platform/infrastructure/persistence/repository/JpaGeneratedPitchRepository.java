package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.GeneratedPitchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaGeneratedPitchRepository extends JpaRepository<GeneratedPitchEntity, UUID> {
    List<GeneratedPitchEntity> findByUserIdAndPitchTypeOrderByGeneratedAtDesc(UUID userId, String pitchType);
    Optional<GeneratedPitchEntity> findFirstByUserIdAndPitchTypeOrderByGeneratedAtDesc(UUID userId, String pitchType);
}
