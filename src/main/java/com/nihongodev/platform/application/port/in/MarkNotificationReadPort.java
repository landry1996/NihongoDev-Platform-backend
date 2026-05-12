package com.nihongodev.platform.application.port.in;

import java.util.UUID;

public interface MarkNotificationReadPort {
    void markAsRead(UUID notificationId, UUID userId);
    void markAllAsRead(UUID userId);
}
