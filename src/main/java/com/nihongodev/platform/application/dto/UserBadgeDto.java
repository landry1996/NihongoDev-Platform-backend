package com.nihongodev.platform.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserBadgeDto(
    UUID id,
    BadgeDto badge,
    LocalDateTime earnedAt,
    boolean showcased
) {}
