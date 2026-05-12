package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.Notification;
import com.nihongodev.platform.domain.model.NotificationChannel;
import com.nihongodev.platform.domain.model.NotificationType;
import com.nihongodev.platform.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {

    public NotificationEntity toEntity(Notification domain) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setType(domain.getType().name());
        entity.setChannel(domain.getChannel().name());
        entity.setTitle(domain.getTitle());
        entity.setMessage(domain.getMessage());
        entity.setRead(domain.isRead());
        entity.setEmailSent(domain.isEmailSent());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setReadAt(domain.getReadAt());
        return entity;
    }

    public Notification toDomain(NotificationEntity entity) {
        Notification n = new Notification();
        n.setId(entity.getId());
        n.setUserId(entity.getUserId());
        n.setType(NotificationType.valueOf(entity.getType()));
        n.setChannel(NotificationChannel.valueOf(entity.getChannel()));
        n.setTitle(entity.getTitle());
        n.setMessage(entity.getMessage());
        n.setRead(entity.isRead());
        n.setEmailSent(entity.isEmailSent());
        n.setCreatedAt(entity.getCreatedAt());
        n.setReadAt(entity.getReadAt());
        return n;
    }
}
