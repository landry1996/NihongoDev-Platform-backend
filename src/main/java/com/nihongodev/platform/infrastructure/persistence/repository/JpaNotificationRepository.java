package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<NotificationEntity> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);
    long countByUserIdAndReadFalse(UUID userId);
}
