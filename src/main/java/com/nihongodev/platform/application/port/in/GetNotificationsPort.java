package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface GetNotificationsPort {
    List<NotificationDto> getAll(UUID userId);
    List<NotificationDto> getUnread(UUID userId);
    long countUnread(UUID userId);
}
