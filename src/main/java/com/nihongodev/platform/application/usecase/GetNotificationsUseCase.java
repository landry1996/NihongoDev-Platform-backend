package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.NotificationDto;
import com.nihongodev.platform.application.port.in.GetNotificationsPort;
import com.nihongodev.platform.application.port.out.NotificationRepositoryPort;
import com.nihongodev.platform.domain.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetNotificationsUseCase implements GetNotificationsPort {

    private final NotificationRepositoryPort notificationRepository;

    public GetNotificationsUseCase(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<NotificationDto> getAll(UUID userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<NotificationDto> getUnread(UUID userId) {
        return notificationRepository.findUnreadByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public long countUnread(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(
                n.getId(), n.getUserId(), n.getType().name(), n.getChannel().name(),
                n.getTitle(), n.getMessage(), n.isRead(), n.isEmailSent(),
                n.getCreatedAt(), n.getReadAt()
        );
    }
}
