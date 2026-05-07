package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CvProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCvProfileRepository extends JpaRepository<CvProfileEntity, UUID> {
    Optional<CvProfileEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
