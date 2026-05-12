package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    List<Notification> findByUserId(UUID userId);
    List<Notification> findUnreadByUserId(UUID userId);
    long countUnreadByUserId(UUID userId);
}
