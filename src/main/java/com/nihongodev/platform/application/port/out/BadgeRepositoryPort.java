package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.Badge;
import com.nihongodev.platform.domain.model.BadgeCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BadgeRepositoryPort {
    Badge save(Badge badge);
    Optional<Badge> findById(UUID id);
    Optional<Badge> findByCode(String code);
    List<Badge> findAll();
    List<Badge> findByCategory(BadgeCategory category);
    List<Badge> findByIds(List<UUID> ids);
}
