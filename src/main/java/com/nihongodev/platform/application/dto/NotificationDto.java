package com.nihongodev.platform.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        UUID userId,
        String type,
        String channel,
        String title,
        String message,
        boolean read,
        boolean emailSent,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {}
