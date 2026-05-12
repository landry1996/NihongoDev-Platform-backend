package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.EmailSenderPort;
import com.nihongodev.platform.application.port.out.NotificationRepositoryPort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendNotificationUseCaseTest {

    @Mock
    private NotificationRepositoryPort notificationRepository;

    @Mock
    private EmailSenderPort emailSender;

    @Mock
    private UserRepositoryPort userRepository;

    private SendNotificationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SendNotificationUseCase(notificationRepository, emailSender, userRepository);
    }

    @Test
    void shouldSaveNotificationWithInAppChannel() {
        UUID userId = UUID.randomUUID();
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.send(userId, NotificationType.WELCOME, NotificationChannel.IN_APP, "Title", "Message");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.WELCOME);
        verify(emailSender, never()).sendHtml(any(), any(), any());
    }

    @Test
    void shouldSendEmailWhenChannelIsBoth() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFirstName("Taro");
        user.setLastName("Yamada");

        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        useCase.send(userId, NotificationType.BADGE_EARNED, NotificationChannel.BOTH, "Badge!", "You earned a badge");

        verify(emailSender).sendHtml(eq("test@example.com"), eq("Badge!"), any());
        verify(notificationRepository, times(2)).save(any());
    }

    @Test
    void shouldSendEmailWhenChannelIsEmail() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("dev@nihongodev.com");
        user.setFirstName("Hanako");

        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        useCase.send(userId, NotificationType.WELCOME, NotificationChannel.EMAIL, "Welcome", "Hello");

        verify(emailSender).sendHtml(eq("dev@nihongodev.com"), eq("Welcome"), any());
    }

    @Test
    void shouldNotFailWhenEmailSendingFails() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("fail@example.com");

        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("SMTP error")).when(emailSender).sendHtml(any(), any(), any());

        useCase.send(userId, NotificationType.WELCOME, NotificationChannel.BOTH, "Title", "Msg");

        verify(notificationRepository, atLeastOnce()).save(any());
    }
}
