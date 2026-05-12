package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void shouldCreateNotification() {
        UUID userId = UUID.randomUUID();
        Notification n = Notification.create(userId, NotificationType.WELCOME, NotificationChannel.BOTH, "Welcome", "Hello");

        assertThat(n.getId()).isNotNull();
        assertThat(n.getUserId()).isEqualTo(userId);
        assertThat(n.getType()).isEqualTo(NotificationType.WELCOME);
        assertThat(n.getChannel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(n.isRead()).isFalse();
        assertThat(n.isEmailSent()).isFalse();
        assertThat(n.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldMarkAsRead() {
        Notification n = Notification.create(UUID.randomUUID(), NotificationType.BADGE_EARNED, NotificationChannel.IN_APP, "T", "M");

        n.markAsRead();

        assertThat(n.isRead()).isTrue();
        assertThat(n.getReadAt()).isNotNull();
    }

    @Test
    void shouldMarkEmailSent() {
        Notification n = Notification.create(UUID.randomUUID(), NotificationType.WELCOME, NotificationChannel.BOTH, "T", "M");

        n.markEmailSent();

        assertThat(n.isEmailSent()).isTrue();
    }

    @Test
    void shouldSendEmailWhenChannelIsBoth() {
        Notification n = Notification.create(UUID.randomUUID(), NotificationType.WELCOME, NotificationChannel.BOTH, "T", "M");
        assertThat(n.shouldSendEmail()).isTrue();
    }

    @Test
    void shouldSendEmailWhenChannelIsEmail() {
        Notification n = Notification.create(UUID.randomUUID(), NotificationType.WELCOME, NotificationChannel.EMAIL, "T", "M");
        assertThat(n.shouldSendEmail()).isTrue();
    }

    @Test
    void shouldNotSendEmailWhenChannelIsInApp() {
        Notification n = Notification.create(UUID.randomUUID(), NotificationType.WELCOME, NotificationChannel.IN_APP, "T", "M");
        assertThat(n.shouldSendEmail()).isFalse();
    }

    @Test
    void shouldNotSendEmailWhenAlreadySent() {
        Notification n = Notification.create(UUID.randomUUID(), NotificationType.WELCOME, NotificationChannel.BOTH, "T", "M");
        n.markEmailSent();
        assertThat(n.shouldSendEmail()).isFalse();
    }
}
