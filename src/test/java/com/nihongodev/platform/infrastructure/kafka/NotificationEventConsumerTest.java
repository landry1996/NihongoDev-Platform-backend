package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.SendNotificationPort;
import com.nihongodev.platform.domain.event.*;
import com.nihongodev.platform.domain.model.NotificationChannel;
import com.nihongodev.platform.domain.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventConsumerTest {

    @Mock
    private SendNotificationPort sendNotificationPort;

    private NotificationEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new NotificationEventConsumer(sendNotificationPort);
    }

    @Test
    void shouldSendWelcomeNotificationOnUserRegistered() {
        UserRegisteredEvent event = UserRegisteredEvent.of(UUID.randomUUID(), "test@dev.com", "Taro", "STUDENT");

        consumer.handleUserRegistered(event);

        verify(sendNotificationPort).send(
                eq(event.userId()),
                eq(NotificationType.WELCOME),
                eq(NotificationChannel.BOTH),
                anyString(),
                contains("Taro")
        );
    }

    @Test
    void shouldSendBadgeNotificationOnBadgeEarned() {
        BadgeEarnedEvent event = BadgeEarnedEvent.create(UUID.randomUUID(), UUID.randomUUID(), "FIRST_QUIZ", "初クイズ", 100);

        consumer.handleBadgeEarned(event);

        verify(sendNotificationPort).send(
                eq(event.userId()),
                eq(NotificationType.BADGE_EARNED),
                eq(NotificationChannel.BOTH),
                contains("初クイズ"),
                contains("100 XP")
        );
    }

    @Test
    void shouldSendQuizCompletedNotification() {
        QuizCompletedEvent event = QuizCompletedEvent.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Kanji Quiz", 85.0, true, 5, "CLASSIC");

        consumer.handleQuizCompleted(event);

        verify(sendNotificationPort).send(
                eq(event.userId()),
                eq(NotificationType.QUIZ_COMPLETED),
                eq(NotificationChannel.IN_APP),
                anyString(),
                contains("85")
        );
    }

    @Test
    void shouldSendInterviewCompletedNotification() {
        InterviewCompletedEvent event = InterviewCompletedEvent.of(UUID.randomUUID(), UUID.randomUUID(),
                "TECH_JAVA", 72.0, 600, true);

        consumer.handleInterviewCompleted(event);

        verify(sendNotificationPort).send(
                eq(event.userId()),
                eq(NotificationType.INTERVIEW_COMPLETED),
                eq(NotificationChannel.IN_APP),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldSendPitchGeneratedNotification() {
        PitchGeneratedEvent event = PitchGeneratedEvent.of(UUID.randomUUID(), UUID.randomUUID(), "ENGLISH_PITCH");

        consumer.handlePitchGenerated(event);

        verify(sendNotificationPort).send(
                eq(event.userId()),
                eq(NotificationType.PITCH_GENERATED),
                eq(NotificationChannel.IN_APP),
                anyString(),
                contains("english pitch")
        );
    }
}
