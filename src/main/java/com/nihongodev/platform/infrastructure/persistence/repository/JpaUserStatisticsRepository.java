package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.UserStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserStatisticsRepository extends JpaRepository<UserStatisticsEntity, UUID> {
    Optional<UserStatisticsEntity> findByUserId(UUID userId);
}
