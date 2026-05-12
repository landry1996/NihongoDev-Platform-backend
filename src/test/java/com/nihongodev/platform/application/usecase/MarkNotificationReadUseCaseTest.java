package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.NotificationRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Notification;
import com.nihongodev.platform.domain.model.NotificationChannel;
import com.nihongodev.platform.domain.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkNotificationReadUseCaseTest {

    @Mock
    private NotificationRepositoryPort notificationRepository;

    private MarkNotificationReadUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new MarkNotificationReadUseCase(notificationRepository);
    }

    @Test
    void shouldMarkNotificationAsRead() {
        UUID userId = UUID.randomUUID();
        Notification notification = Notification.create(userId, NotificationType.WELCOME, NotificationChannel.IN_APP, "T", "M");
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.markAsRead(notification.getId(), userId);

        assertThat(notification.isRead()).isTrue();
        assertThat(notification.getReadAt()).isNotNull();
        verify(notificationRepository).save(notification);
    }

    @Test
    void shouldThrowWhenNotificationNotFound() {
        UUID notifId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(notificationRepository.findById(notifId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.markAsRead(notifId, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenUserDoesNotOwnNotification() {
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Notification notification = Notification.create(ownerId, NotificationType.WELCOME, NotificationChannel.IN_APP, "T", "M");
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> useCase.markAsRead(notification.getId(), otherUserId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldMarkAllAsRead() {
        UUID userId = UUID.randomUUID();
        Notification n1 = Notification.create(userId, NotificationType.WELCOME, NotificationChannel.IN_APP, "T1", "M1");
        Notification n2 = Notification.create(userId, NotificationType.BADGE_EARNED, NotificationChannel.IN_APP, "T2", "M2");
        when(notificationRepository.findUnreadByUserId(userId)).thenReturn(List.of(n1, n2));
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.markAllAsRead(userId);

        assertThat(n1.isRead()).isTrue();
        assertThat(n2.isRead()).isTrue();
        verify(notificationRepository, times(2)).save(any());
    }
}
