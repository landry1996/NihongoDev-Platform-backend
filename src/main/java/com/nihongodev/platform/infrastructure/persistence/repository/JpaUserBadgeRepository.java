package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.UserBadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserBadgeRepository extends JpaRepository<UserBadgeEntity, UUID> {

    List<UserBadgeEntity> findByUserId(UUID userId);

    Optional<UserBadgeEntity> findByUserIdAndBadgeId(UUID userId, UUID badgeId);

    boolean existsByUserIdAndBadgeId(UUID userId, UUID badgeId);

    int countByUserId(UUID userId);
}
