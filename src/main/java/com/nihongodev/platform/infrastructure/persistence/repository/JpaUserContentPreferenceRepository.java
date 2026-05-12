package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.UserContentPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserContentPreferenceRepository extends JpaRepository<UserContentPreferenceEntity, UUID> {
    Optional<UserContentPreferenceEntity> findByUserId(UUID userId);
}
