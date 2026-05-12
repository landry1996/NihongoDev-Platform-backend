package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.NotificationDto;
import com.nihongodev.platform.application.port.out.NotificationRepositoryPort;
import com.nihongodev.platform.domain.model.Notification;
import com.nihongodev.platform.domain.model.NotificationChannel;
import com.nihongodev.platform.domain.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNotificationsUseCaseTest {

    @Mock
    private NotificationRepositoryPort notificationRepository;

    private GetNotificationsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetNotificationsUseCase(notificationRepository);
    }

    @Test
    void shouldReturnAllNotifications() {
        UUID userId = UUID.randomUUID();
        Notification n1 = Notification.create(userId, NotificationType.WELCOME, NotificationChannel.IN_APP, "T1", "M1");
        Notification n2 = Notification.create(userId, NotificationType.BADGE_EARNED, NotificationChannel.BOTH, "T2", "M2");
        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(n1, n2));

        List<NotificationDto> result = useCase.getAll(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo("WELCOME");
    }

    @Test
    void shouldReturnUnreadNotifications() {
        UUID userId = UUID.randomUUID();
        Notification n = Notification.create(userId, NotificationType.LEVEL_UP, NotificationChannel.IN_APP, "Level Up", "XP");
        when(notificationRepository.findUnreadByUserId(userId)).thenReturn(List.of(n));

        List<NotificationDto> result = useCase.getUnread(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).read()).isFalse();
    }

    @Test
    void shouldCountUnreadNotifications() {
        UUID userId = UUID.randomUUID();
        when(notificationRepository.countUnreadByUserId(userId)).thenReturn(5L);

        long count = useCase.countUnread(userId);

        assertThat(count).isEqualTo(5);
    }
}
