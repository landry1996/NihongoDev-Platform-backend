package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.PublicProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaPublicProfileRepository extends JpaRepository<PublicProfileEntity, UUID> {

    Optional<PublicProfileEntity> findByUserId(UUID userId);

    Optional<PublicProfileEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
