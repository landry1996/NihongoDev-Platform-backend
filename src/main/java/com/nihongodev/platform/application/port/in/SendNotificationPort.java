package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.model.NotificationChannel;
import com.nihongodev.platform.domain.model.NotificationType;

import java.util.UUID;

public interface SendNotificationPort {
    void send(UUID userId, NotificationType type, NotificationChannel channel, String title, String message);
}
