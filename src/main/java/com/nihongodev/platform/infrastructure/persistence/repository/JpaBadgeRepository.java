package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaBadgeRepository extends JpaRepository<BadgeEntity, UUID> {

    Optional<BadgeEntity> findByCode(String code);

    List<BadgeEntity> findByCategory(String category);

    List<BadgeEntity> findByIdIn(List<UUID> ids);
}
