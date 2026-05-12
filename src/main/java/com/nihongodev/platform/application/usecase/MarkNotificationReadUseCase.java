package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.MarkNotificationReadPort;
import com.nihongodev.platform.application.port.out.NotificationRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Notification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MarkNotificationReadUseCase implements MarkNotificationReadPort {

    private final NotificationRepositoryPort notificationRepository;

    public MarkNotificationReadUseCase(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("Cannot mark another user's notification as read");
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        notificationRepository.findUnreadByUserId(userId).forEach(notification -> {
            notification.markAsRead();
            notificationRepository.save(notification);
        });
    }
}
