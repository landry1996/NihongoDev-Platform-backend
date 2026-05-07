package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.UserProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserProgressRepository extends JpaRepository<UserProgressEntity, UUID> {
    Optional<UserProgressEntity> findByUserId(UUID userId);
}
