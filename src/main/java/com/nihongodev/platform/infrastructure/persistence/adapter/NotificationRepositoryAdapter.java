package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.NotificationRepositoryPort;
import com.nihongodev.platform.domain.model.Notification;
import com.nihongodev.platform.infrastructure.persistence.mapper.NotificationPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaNotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final JpaNotificationRepository jpaRepository;
    private final NotificationPersistenceMapper mapper;

    public NotificationRepositoryAdapter(JpaNotificationRepository jpaRepository,
                                         NotificationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(notification)));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Notification> findUnreadByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndReadFalse(userId);
    }
}
