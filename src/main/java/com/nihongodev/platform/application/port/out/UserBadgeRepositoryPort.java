package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.UserBadge;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserBadgeRepositoryPort {
    UserBadge save(UserBadge userBadge);
    List<UserBadge> findByUserId(UUID userId);
    Optional<UserBadge> findByUserIdAndBadgeId(UUID userId, UUID badgeId);
    boolean existsByUserIdAndBadgeId(UUID userId, UUID badgeId);
    int countByUserId(UUID userId);
}
